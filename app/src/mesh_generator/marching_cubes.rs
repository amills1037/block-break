use bevy::prelude::*;

use super::marching_cubes_const::*;

use super::MeshGenerator;
use super::voxel_mesh::VoxelMesh;
use crate::terrain::voxel_data::{CHUNK_X_LENGTH, CHUNK_Y_LENGTH, CHUNK_Z_LENGTH, VoxelData};

pub struct MarchingCubesMeshGenerator;

impl MeshGenerator for MarchingCubesMeshGenerator {
    fn generate_mesh(voxel_data: &VoxelData, voxel_mesh: &mut VoxelMesh) {
        let mut tri_count = 0;

        let mut corners: [Vec4; 8] = [Vec4::default(); 8];
        // NativeArray<float4> corners = new NativeArray<float4>(8, Allocator.Temp);

        for zi in 0..CHUNK_Z_LENGTH {
            for yi in 0..CHUNK_Y_LENGTH {
                for xi in 0..CHUNK_X_LENGTH {
                    let z = zi as f32;
                    let y = yi as f32;
                    let x = xi as f32;

                    corners[0] = Vec4::new(x, y, z, voxel_data.get_data(xi, yi, zi) as f32);
                    corners[1] =
                        Vec4::new(x + 1., y, z, voxel_data.get_data(xi + 1, yi, zi) as f32);
                    corners[2] = Vec4::new(
                        x + 1.,
                        y,
                        z + 1.,
                        voxel_data.get_data(xi + 1, yi, zi + 1) as f32,
                    );
                    corners[3] =
                        Vec4::new(x, y, z + 1., voxel_data.get_data(xi, yi, zi + 1) as f32);
                    corners[4] =
                        Vec4::new(x, y + 1., z, voxel_data.get_data(xi, yi + 1, zi) as f32);
                    corners[5] = Vec4::new(
                        x + 1.,
                        y + 1.,
                        z,
                        voxel_data.get_data(xi + 1, yi + 1, zi) as f32,
                    );
                    corners[6] = Vec4::new(
                        x + 1.,
                        y + 1.,
                        z + 1.,
                        voxel_data.get_data(xi + 1, yi + 1, zi + 1) as f32,
                    );
                    corners[7] = Vec4::new(
                        x,
                        y + 1.,
                        z + 1.,
                        voxel_data.get_data(xi, yi + 1, zi + 1) as f32,
                    );

                    let mut cube_index = 0;
                    if corners[0].w > 0. {
                        cube_index |= 1
                    }
                    if corners[1].w > 0. {
                        cube_index |= 2
                    }
                    if corners[2].w > 0. {
                        cube_index |= 4
                    }
                    if corners[3].w > 0. {
                        cube_index |= 8
                    }
                    if corners[4].w > 0. {
                        cube_index |= 16
                    }
                    if corners[5].w > 0. {
                        cube_index |= 32
                    }
                    if corners[6].w > 0. {
                        cube_index |= 64
                    }
                    if corners[7].w > 0. {
                        cube_index |= 128
                    }

                    let mut i = 0;
                    while TRI_TABLE[cube_index * 16 + i] != -1 {
                        let ci = cube_index * 16 + i;
                        let a0 = CORNER_INDEX_A_FROM_EDGE[TRI_TABLE[ci] as usize];
                        let b0 = CORNER_INDEX_B_FROM_EDGE[TRI_TABLE[ci] as usize];

                        let a1 = CORNER_INDEX_A_FROM_EDGE[TRI_TABLE[ci + 1] as usize];
                        let b1 = CORNER_INDEX_B_FROM_EDGE[TRI_TABLE[ci + 1] as usize];

                        let a2 = CORNER_INDEX_A_FROM_EDGE[TRI_TABLE[ci + 2] as usize];
                        let b2 = CORNER_INDEX_B_FROM_EDGE[TRI_TABLE[ci + 2] as usize];

                        let vertex_a = interpolate_verts(corners[a0], corners[b0]);
                        let vertex_b = interpolate_verts(corners[a1], corners[b1]);
                        let vertex_c = interpolate_verts(corners[a2], corners[b2]);

                        voxel_mesh.vertices.push(vertex_a.to_array());
                        voxel_mesh.vertices.push(vertex_b.to_array());
                        voxel_mesh.vertices.push(vertex_c.to_array());

                        voxel_mesh.triangles.push(tri_count);
                        tri_count += 1;
                        voxel_mesh.triangles.push(tri_count);
                        tri_count += 1;
                        voxel_mesh.triangles.push(tri_count);
                        tri_count += 1;

                        voxel_mesh.uvs.push([0., 0.]);
                        voxel_mesh.uvs.push([0., 1.]);
                        voxel_mesh.uvs.push([1., 0.]);

                        //Calculate normal
                        let ab = vertex_b - vertex_a;
                        let ac = vertex_c - vertex_a;

                        // Calculate the cross product (AB × AC)
                        let normal = ab.cross(ac).normalize().to_array();

                        voxel_mesh.normals.push(normal);
                        voxel_mesh.normals.push(normal);
                        voxel_mesh.normals.push(normal);
                        i += 3;
                    }
                }
            }
        }
    }
}

fn interpolate_verts(v1: Vec4, v2: Vec4) -> Vec3 {
    let t = (ISO_LEVEL - v1.w) / (v2.w - v1.w);
    return v1.xyz() + t * (v2.xyz() - v1.xyz());
}
