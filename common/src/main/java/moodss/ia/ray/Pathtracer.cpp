// bidirectional-path-tracing.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"

#include <SFML/OpenGL.hpp>
#include <SFML/Graphics/Shader.hpp>
#include "vec3.h"
#include "ray.h"
#include "Object.h"
#include "sphere.h"
#include "path.h"
#include "accelerationStructure.h"
#include "camera.h"
#include "util.h"
#include "material.h"
#include "triangle.h"
#include "mesh.h"
#include "model.h"
#include "SoundNode.h"
#include "AudioStream.h"
#include "ModelLoader.h"
#include "Octree.h"
#include "Light.h"
#include "World.h"
#include "Renderer.h"


#include "Presets.h"

#define STB_IMAGE_WRITE_IMPLEMENTATION
#define STBI_MSC_SECURE_CRT
#include "3rd_party\stb\stb_image_write.h"

#include <SFML/Audio.hpp>
// TODO Add ClangFormat

int samplesPerSecond = 0;  // Global that needs to be set from file

float const reverbLength = 2.f;
int const rays = 5000;

float const airDampingCoeff = 0.01f;
float const minEnergy = 0.005f;


void TraceForward(Ray const& r, World* world, float* impulseResponse, float timePassed /* seconds */, Vec3 color, std::vector<Vec3>& currentPath)
{
	HitRecord rec;
	if (world->mOctree->Hit(r, 0.00001f, FLT_MAX, rec)) {
		// Calculate the time it took for the ray to reach here
		float distanceTravelled = rec.t / r.B.length();
		timePassed += GetTimeFromDistance(distanceTravelled);

		// Calculate the damping for this distance
		float k = exp(airDampingCoeff * distanceTravelled); // [1, inf] based on distance
		Vec3 damping = Vec3(1.f / k, 1.f / k, 1.f / k);

		// Store the location of the collision in the path
		currentPath.push_back(rec.p);

		// Test if we have reached the maximum time to recieve a ray
		if (timePassed > reverbLength) {
			return;
		}

		if (isType<Listener*>(rec.material)) {
			// record the collision and return
			int sampleIndex = (int)(samplesPerSecond * timePassed);
			impulseResponse[sampleIndex] += color.x() * damping.x(); // Only one component for now

			// Add this path to be drawn
			LockWorld();
			world->AddNonCollidingObject(new Path(currentPath, new Solid(Vec3(0, 0, 0), Vec3(sqrt(color.x()), sqrt(color.y()), sqrt(color.z())))));
			UnlockWorld();

			printf("Collision with listener at t=%f with energy=%f\n", timePassed, color.x());
			return;
		}

		if (isType<Solid*>(rec.material)) {
			Solid* mat = dynamic_cast<Solid*>(rec.material);
			Vec3 scactteredDir = rec.normal + RandInSphere();
			scactteredDir.normalize();
			Ray scattered(rec.p, scactteredDir);
			Vec3 scatteredColor = color * mat->mAttenuation * damping;

			// If this ray will have no energy, stop tracing
			if (scatteredColor.x() < minEnergy) {
				return;
			}

			TraceForward(scattered, world, impulseResponse, timePassed, scatteredColor, currentPath);
			return;
		}
	} else { // Ray hit nothing, sound is lost
		printf("lost ray\n");
		return;
	}
}

void AudioThread(World* world) {
	// Load the sound and gather its raw data
	sf::SoundBuffer buffer;
	buffer.loadFromFile("Sounds/hellokirkquiet.wav");
	buffer.saveToFile("Output/source.wav");
	samplesPerSecond                   = buffer.getSampleRate();
	int              sourceSampleCount = buffer.getSampleCount();
	sf::Int16 const* sourceSamples     = buffer.getSamples();

	sf::Sound test;
	test.setBuffer(buffer);
	test.play();

	// Set up the array for impulse response
	int const impulseSampleCount = (int)(samplesPerSecond * reverbLength);
	float*    impulseResponse    = (float*)calloc(impulseSampleCount, sizeof(float));

	for (int ii = 0; ii < world->mNonCollidingObjects.size(); ++ii) {
		Object* testObject = world->mNonCollidingObjects[ii];
		SoundNode* sound = dynamic_cast<SoundNode*>(testObject);
		if (sound) {
			std::vector<Vec3> path;
			for (int sample = 0; sample < rays; ++sample) {

				// Create a vector to keep track of how the ray reaches the listener
				path.clear();
				path.push_back(sound->center);

				// Choose a random direction
				Ray r(sound->center, RandOnSphere());
				Vec3 origColor(1.f, 1.f, 1.f);
				TraceForward(r, world, impulseResponse, 0.f, origColor, path);
			}
		}
	}

	test.stop();
	ProcessSound(impulseResponse, impulseSampleCount, sourceSamples, sourceSampleCount, samplesPerSecond);

	delete[] impulseResponse;
}


int main()
{
	srand(time(NULL));

	World* world;
	// Load a preset
	LoadPreset(&world, kBox);

	std::thread audioThread(AudioThread, world);

	// Render on the main thread
	RenderThread(world);

	audioThread.join();

	return 0;
}