use super::MeshGenerator;
use super::mesh_cube::VoxelMeshCube;
use super::voxel_mesh::VoxelMesh;
use crate::terrain::voxel_data::{
    CHUNK_X_LENGTH, CHUNK_Y_LENGTH, CHUNK_Z_LENGTH, EMPTY_VOXEL, VoxelData,
};

pub struct GreedyMeshGenerator;

impl MeshGenerator for GreedyMeshGenerator {
    fn generate_mesh(voxel_data: &VoxelData, voxel_mesh: &mut VoxelMesh) {
        let mut x = 0;
        let mut y = 0;
        let mut z = 0;

        while x < CHUNK_X_LENGTH {
            z = 0;
            while z < CHUNK_Z_LENGTH {
                let mask_y = voxel_data.get_mask(x, z);
                y = 0;
                while y < CHUNK_Y_LENGTH && mask_y != 0 {
                    let yl = 1;
                    let (solid, xl, yl, zl) =
                        GreedyMeshGenerator::generate_solid(voxel_data, x, y, z);

                    if solid {
                        VoxelMeshCube::push_triangles(voxel_mesh, voxel_mesh.vertices.len() as u32);
                        VoxelMeshCube::push_vertices_with_length(
                            voxel_mesh,
                            [x as f32, y as f32, z as f32],
                            [xl as f32, yl as f32, zl as f32],
                        );
                        VoxelMeshCube::push_normals(voxel_mesh);
                        VoxelMeshCube::push_uvs(voxel_mesh);
                    }

                    // maskY = GetMask(blockMaskY, x, z);
                    y += yl;
                }
                z += 1;
            }
            x += 1;
        }
    }
}

impl GreedyMeshGenerator {
    fn generate_solid(voxel_data: &VoxelData, x: i32, y: i32, z: i32) -> (bool, i32, i32, i32) {
        let block = voxel_data.get_data(x, y, z);
        let solid = block != EMPTY_VOXEL;

        let mut y = y;
        let xl = 1;
        let mut yl = 1;
        let zl = 1;

        y += 1;
        while y < CHUNK_Y_LENGTH {
            let next_block = voxel_data.get_data(x, y, z);

            if block != next_block {
                break;
            }

            yl += 1;
            y += 1;
        }

        return (solid, xl, yl, zl);
    }
}

//         NativeArray<uint> blockMaskY = new NativeArray<uint>(Data.BlockMaskY.Length, Allocator.Temp);
//         blockMaskY.CopyFrom(Data.BlockMaskY);

//         int x = 0;
//         int y;
//         int z;

//     public uint GetMask(NativeArray<uint> masks, int x, int z)
//     {
//         return masks[z * VoxelData.DataXLength + x];
//     }

// }
