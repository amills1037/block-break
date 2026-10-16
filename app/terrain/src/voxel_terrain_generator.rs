use crate::voxel_data::*;
use noise::noise_generator::NoiseGenerator;

pub trait TerrainGenerator {
    fn generate(&self, voxel_data: &mut VoxelData);
}

#[allow(dead_code)]
pub struct FlatTerrainGenerator {
    pub height: i32,
    pub solid_voxel: u8,
    pub empty_voxel: u8,
}

impl FlatTerrainGenerator {
    #[allow(dead_code)]
    pub fn new(height: i32) -> Self {
        Self {
            height,
            solid_voxel: SOLID_VOXEL,
            empty_voxel: EMPTY_VOXEL,
        }
    }
}

impl Default for FlatTerrainGenerator {
    fn default() -> Self {
        Self {
            height: CHUNK_Y_LENGTH / 2,
            solid_voxel: SOLID_VOXEL,
            empty_voxel: EMPTY_VOXEL,
        }
    }
}

impl TerrainGenerator for FlatTerrainGenerator {
    fn generate(&self, voxel_data: &mut VoxelData) {
        for z in 0..DATA_Z_LENGTH {
            for x in 0..DATA_Y_LENGTH {
                for y in 0..DATA_X_LENGTH {
                    voxel_data.set_data(
                        x,
                        y,
                        z,
                        if y < self.height {
                            self.solid_voxel
                        } else {
                            self.empty_voxel
                        },
                    );
                }
            }
        }
    }
}

pub struct HeightMapTerrainGenerator {
    solid_voxel: u8,
    empty_voxel: u8,
}

impl HeightMapTerrainGenerator {
    pub fn new() -> Self {
        Self {
            solid_voxel: SOLID_VOXEL,
            empty_voxel: EMPTY_VOXEL,
        }
    }
}

impl Default for HeightMapTerrainGenerator {
    fn default() -> Self {
        Self {
            solid_voxel: SOLID_VOXEL,
            empty_voxel: EMPTY_VOXEL,
        }
    }
}

impl TerrainGenerator for HeightMapTerrainGenerator {
    fn generate(&self, voxel_data: &mut VoxelData) {
        let mut noise_map: Vec<f32> = vec![0.0; DATA_XYZ_LENGTH as usize];

        let noise_generator = NoiseGenerator::new(12345);

        noise_generator.generate_noise_map(
            (
                (voxel_data.coords.x * CHUNK_X_LENGTH) as f32,
                (voxel_data.coords.z * CHUNK_Z_LENGTH) as f32,
            ),
            &mut noise_map,
            DATA_X_LENGTH as usize,
            DATA_Z_LENGTH as usize,
        );

        for z in 0..DATA_Z_LENGTH {
            for x in 0..DATA_Z_LENGTH {
                let height = (noise_map[(z * DATA_X_LENGTH + x) as usize] * CHUNK_Y_LENGTH as f32)
                    .round() as i32;

                for y in 0..DATA_X_LENGTH {
                    voxel_data.set_data(
                        x,
                        y,
                        z,
                        if y < height {
                            self.solid_voxel
                        } else {
                            self.empty_voxel
                        },
                    );
                }
            }
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn create_flat_terrain_generator() {
        let generator = FlatTerrainGenerator::new(16);

        assert_eq!(generator.height, 16);
    }
}
