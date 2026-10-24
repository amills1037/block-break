use super::mesh_cube::VoxelMeshCube;
use super::mesh_generator::MeshGenerator;
use super::voxel_chunk_mesh::VoxelChunkMesh;
use terrain::voxel_data::{CHUNK_X_LENGTH, CHUNK_Y_LENGTH, CHUNK_Z_LENGTH, VoxelData};

pub struct NaiveMeshGenerator;

impl MeshGenerator for NaiveMeshGenerator {
    fn generate_mesh(voxel_data: &VoxelData, voxel_mesh: &mut VoxelChunkMesh) {
        for z in 0..CHUNK_Z_LENGTH {
            for x in 0..CHUNK_X_LENGTH {
                for y in 0..CHUNK_Y_LENGTH {
                    if voxel_data.get_data(x, y, z) > 0 {
                        // Add triangles first or triangles index will be 24 larger
                        VoxelMeshCube::push_triangles(voxel_mesh, voxel_mesh.vertices.len() as u32);
                        VoxelMeshCube::push_vertices(voxel_mesh, [x as f32, y as f32, z as f32]);
                        VoxelMeshCube::push_normals(voxel_mesh);
                        VoxelMeshCube::push_uvs(voxel_mesh);
                    }
                }
            }
        }
    }
}
