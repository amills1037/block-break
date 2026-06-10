// use noise::*;

use noise_functions::{Noise, Simplex};
use rand::RngExt;
use rand_pcg::Mcg128Xsl64;

const OCTABES_MIN_RANDOM: f32 = -100000.0;
const OCTABES_MAX_RANDOM: f32 = 100000.0;

pub struct NoiseGenerator {
    seed: u32,
    scale: f32,
    octaves: u32,
    persistence: f32,
    lacunarity: f32,
}

impl NoiseGenerator {
    pub fn new(seed: u32) -> Self {
        Self::new_params(
            seed, 100.0, // scale
            3,     // octaves
            2.0,   // persistence
            0.5,   // lacunarity
        )
    }

    pub fn new_params(
        seed: u32,
        scale: f32,
        octaves: u32,
        persistence: f32,
        lacunarity: f32,
    ) -> Self {
        Self {
            seed: seed,
            scale: scale,
            octaves: octaves,
            persistence: persistence,
            lacunarity: lacunarity,
        }
    }
}

impl NoiseGenerator {
    pub fn generate_noise_map(
        &self,
        offset: (f32, f32),
        noise_map: &mut Vec<f32>,
        map_width: usize,
        map_height: usize,
    ) {
        let mut octive_offsets: Vec<f32> = vec![0.0; (self.octaves * 2) as usize];

        let mut rng = Mcg128Xsl64::new(self.seed as u128);

        let mut max_possible_height: f32 = 0.0;
        let mut amplitude: f32 = 1.0;

        for i in 0..self.octaves {
            let offset_x: f32 = rng.random_range(OCTABES_MIN_RANDOM..OCTABES_MAX_RANDOM) + offset.0;
            let offset_y: f32 = rng.random_range(OCTABES_MIN_RANDOM..OCTABES_MAX_RANDOM) + offset.1;

            octive_offsets[(i * 2) as usize] = offset_x;
            octive_offsets[(i * 2 + 1) as usize] = offset_y;

            max_possible_height += amplitude;
            amplitude *= self.persistence;
        }

        let mut frequency: f32;

        let half_width: f32 = map_width as f32 / 2.0;
        let half_height: f32 = map_width as f32 / 2.0;

        for y in 0..map_height {
            for x in 0..map_width {
                amplitude = 1.0;
                frequency = 1.0;
                let mut noise_height: f32 = 0.0;

                for i in 0..self.octaves {
                    let sample_x = (x as f32 - half_width + octive_offsets[(i * 2) as usize])
                        / self.scale
                        * frequency;
                    let sample_y = (y as f32 - half_height + octive_offsets[(i * 2 + 1) as usize])
                        / self.scale
                        * frequency;

                    let noise_value = Simplex.sample2((sample_x, sample_y)); // ?? * 2 - 1;
                    noise_height += noise_value * amplitude;

                    amplitude *= self.persistence;
                    frequency *= self.lacunarity;
                }

                noise_map[y * map_width + x] = noise_height / max_possible_height;
            }
        }
        // 			for (int i = 0; i < noiseParams.Octaves; i++) {
        // 				float sampleX = (x - halfWidth + octaveOffsets[i].x) / noiseParams.Scale * frequency;
        // 				float sampleY = (y - halfHeight + octaveOffsets[i].y) / noiseParams.Scale * frequency;

        // 				float noiseValue = noise.snoise(new float2(sampleX, sampleY)); // ?? * 2 - 1;
        // 				noiseHeight += noiseValue * amplitude;

        // 				amplitude *= noiseParams.Persistance;
        // 				frequency *= noiseParams.Lacunarity;
        // 			}

        // 			noiseMap [y * mapWidth + x] = noiseHeight / maxPossibleHeight;
        // 		}
        // 	}
    }
    // public static void GenerateNoiseMap(in NoiseParameters noiseParams, in float2 offset, ref NativeArray<float> noiseMap, int mapWidth, int mapHeight)
    //    {
    //        NativeArray<float2> octaveOffsets = new NativeArray<float2>(noiseParams.Octaves, Allocator.Temp);
    //        Unity.Mathematics.Random random = new Unity.Mathematics.Random(noiseParams.Seed);

    //        float maxPossibleHeight = 0;
    // 	float amplitude = 1;

    // 	for (int i = 0; i < noiseParams.Octaves; i++) {
    // 		float offsetX = random.NextFloat (OctavesMinRandom, OctavesMaxRandom) + offset.x;
    // 		float offsetY = random.NextFloat (OctavesMinRandom, OctavesMaxRandom) + offset.y;
    // 		octaveOffsets[i] = new float2 (offsetX, offsetY);

    // 		maxPossibleHeight += amplitude;
    // 		amplitude *= noiseParams.Persistance;
    // 	}

    // 	float frequency;

    // 	float halfWidth = mapWidth / 2f;
    // 	float halfHeight = mapHeight / 2f;

    // 	for (int y = 0; y < mapHeight; y++) {
    // 		for (int x = 0; x < mapWidth; x++) {
    // 			amplitude = 1;
    // 			frequency = 1;
    // 			float noiseHeight = 0;

    // 			for (int i = 0; i < noiseParams.Octaves; i++) {
    // 				float sampleX = (x - halfWidth + octaveOffsets[i].x) / noiseParams.Scale * frequency;
    // 				float sampleY = (y - halfHeight + octaveOffsets[i].y) / noiseParams.Scale * frequency;

    // 				float noiseValue = noise.snoise(new float2(sampleX, sampleY)); // ?? * 2 - 1;
    // 				noiseHeight += noiseValue * amplitude;

    // 				amplitude *= noiseParams.Persistance;
    // 				frequency *= noiseParams.Lacunarity;
    // 			}

    // 			noiseMap [y * mapWidth + x] = noiseHeight / maxPossibleHeight;
    // 		}
    // 	}

    //        octaveOffsets.Dispose();
    // }
}
