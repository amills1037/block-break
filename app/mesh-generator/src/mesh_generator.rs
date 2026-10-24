use crate::voxel_chunk_mesh::VoxelChunkMesh;
use terrain::voxel_data::VoxelData;

pub trait MeshGenerator {
    fn generate_mesh(voxel_data: &VoxelData, voxel_mesh: &mut VoxelChunkMesh);
}
