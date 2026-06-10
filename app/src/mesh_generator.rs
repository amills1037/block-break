use crate::terrain::voxel_data::VoxelData;
use voxel_mesh::VoxelMesh;

mod marching_cubes_const;

pub mod greedy;
pub mod marching_cubes;
pub mod mesh_cube;
pub mod naive;
pub mod voxel_mesh;

pub trait MeshGenerator {
    fn generate_mesh(voxel_data: &VoxelData, voxel_mesh: &mut VoxelMesh);
}
