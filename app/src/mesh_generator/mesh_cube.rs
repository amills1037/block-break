use crate::mesh_generator::voxel_mesh::VoxelMesh;

pub struct VoxelMeshCube {}

impl VoxelMeshCube {
    pub fn push_vertices(voxel_mesh: &mut VoxelMesh, position: [f32; 3]) {
        let x_pos = 0.5 + position[0];
        let y_pos = 0.5 + position[1];
        let z_pos = 0.5 + position[2];

        let x_neg = -0.5 + position[0];
        let y_neg = -0.5 + position[1];
        let z_neg = -0.5 + position[2];

        VoxelMeshCube::push_vertices_with_positions(
            voxel_mesh, x_pos, y_pos, z_pos, x_neg, y_neg, z_neg,
        );
    }

    pub fn push_vertices_with_length(
        voxel_mesh: &mut VoxelMesh,
        position: [f32; 3],
        lengths: [f32; 3],
    ) {
        let x_coord = (lengths[0] - 1.) * 0.5 + position[0];
        let y_coord = (lengths[1] - 1.) * 0.5 + position[1];
        let z_coord = (lengths[2] - 1.) * 0.5 + position[2];

        let x_pos = 0.5 * lengths[0] + x_coord;
        let y_pos = 0.5 * lengths[1] + y_coord;
        let z_pos = 0.5 * lengths[2] + z_coord;

        let x_neg = -0.5 * lengths[0] + x_coord;
        let y_neg = -0.5 * lengths[1] + y_coord;
        let z_neg = -0.5 * lengths[2] + z_coord;

        VoxelMeshCube::push_vertices_with_positions(
            voxel_mesh, x_pos, y_pos, z_pos, x_neg, y_neg, z_neg,
        );
    }

    pub fn push_vertices_with_positions(
        voxel_mesh: &mut VoxelMesh,
        x_pos: f32,
        y_pos: f32,
        z_pos: f32,
        x_neg: f32,
        y_neg: f32,
        z_neg: f32,
    ) {
        //face 1, xy
        voxel_mesh.push_vertex([x_pos, y_neg, z_neg]);
        voxel_mesh.push_vertex([x_neg, y_neg, z_neg]);
        voxel_mesh.push_vertex([x_neg, y_pos, z_neg]);
        voxel_mesh.push_vertex([x_pos, y_pos, z_neg]);

        //face 2, xy
        voxel_mesh.push_vertex([x_pos, y_pos, z_pos]);
        voxel_mesh.push_vertex([x_neg, y_pos, z_pos]);
        voxel_mesh.push_vertex([x_neg, y_neg, z_pos]);
        voxel_mesh.push_vertex([x_pos, y_neg, z_pos]);

        //face 3, yz
        voxel_mesh.push_vertex([x_neg, y_pos, z_neg]);
        voxel_mesh.push_vertex([x_neg, y_neg, z_neg]);
        voxel_mesh.push_vertex([x_neg, y_neg, z_pos]);
        voxel_mesh.push_vertex([x_neg, y_pos, z_pos]);

        //face 4, yz
        voxel_mesh.push_vertex([x_pos, y_pos, z_pos]);
        voxel_mesh.push_vertex([x_pos, y_neg, z_pos]);
        voxel_mesh.push_vertex([x_pos, y_neg, z_neg]);
        voxel_mesh.push_vertex([x_pos, y_pos, z_neg]);

        //face 5, xz
        voxel_mesh.push_vertex([x_neg, y_neg, z_pos]);
        voxel_mesh.push_vertex([x_neg, y_neg, z_neg]);
        voxel_mesh.push_vertex([x_pos, y_neg, z_neg]);
        voxel_mesh.push_vertex([x_pos, y_neg, z_pos]);

        // //face 6, xz
        voxel_mesh.push_vertex([x_pos, y_pos, z_pos]);
        voxel_mesh.push_vertex([x_pos, y_pos, z_neg]);
        voxel_mesh.push_vertex([x_neg, y_pos, z_neg]);
        voxel_mesh.push_vertex([x_neg, y_pos, z_pos]);
    }

    pub fn push_triangles(voxel_mesh: &mut VoxelMesh, offset: u32) {
        // face 1
        voxel_mesh.push_triangles([0 + offset, 1 + offset, 2 + offset]);
        voxel_mesh.push_triangles([2 + offset, 3 + offset, 0 + offset]);

        // face 2
        voxel_mesh.push_triangles([4 + offset, 5 + offset, 6 + offset]);
        voxel_mesh.push_triangles([6 + offset, 7 + offset, 4 + offset]);

        // face 3
        voxel_mesh.push_triangles([8 + offset, 9 + offset, 10 + offset]);
        voxel_mesh.push_triangles([10 + offset, 11 + offset, 8 + offset]);

        // face 4
        voxel_mesh.push_triangles([12 + offset, 13 + offset, 14 + offset]);
        voxel_mesh.push_triangles([14 + offset, 15 + offset, 12 + offset]);

        // face 5
        voxel_mesh.push_triangles([16 + offset, 17 + offset, 18 + offset]);
        voxel_mesh.push_triangles([18 + offset, 19 + offset, 16 + offset]);

        // face 6
        voxel_mesh.push_triangles([20 + offset, 21 + offset, 22 + offset]);
        voxel_mesh.push_triangles([22 + offset, 23 + offset, 20 + offset]);
    }

    pub fn push_normals(voxel_mesh: &mut VoxelMesh) {
        //face 1, xy
        voxel_mesh.push_normals([0., 0., -1.]);
        voxel_mesh.push_normals([0., 0., -1.]);
        voxel_mesh.push_normals([0., 0., -1.]);
        voxel_mesh.push_normals([0., 0., -1.]);

        //face 2, xy
        voxel_mesh.push_normals([0., 0., 1.]);
        voxel_mesh.push_normals([0., 0., 1.]);
        voxel_mesh.push_normals([0., 0., 1.]);
        voxel_mesh.push_normals([0., 0., 1.]);

        //face 3, yz
        voxel_mesh.push_normals([-1., 0., 0.]);
        voxel_mesh.push_normals([-1., 0., 0.]);
        voxel_mesh.push_normals([-1., 0., 0.]);
        voxel_mesh.push_normals([-1., 0., 0.]);

        //face 4, yz
        voxel_mesh.push_normals([1., 0., 0.]);
        voxel_mesh.push_normals([1., 0., 0.]);
        voxel_mesh.push_normals([1., 0., 0.]);
        voxel_mesh.push_normals([1., 0., 0.]);

        //face 5, xz
        voxel_mesh.push_normals([0., -1., 0.]);
        voxel_mesh.push_normals([0., -1., 0.]);
        voxel_mesh.push_normals([0., -1., 0.]);
        voxel_mesh.push_normals([0., -1., 0.]);

        // //face 6, xz
        voxel_mesh.push_normals([0., 1., 0.]);
        voxel_mesh.push_normals([0., 1., 0.]);
        voxel_mesh.push_normals([0., 1., 0.]);
        voxel_mesh.push_normals([0., 1., 0.]);
    }

    pub fn push_uvs(voxel_mesh: &mut VoxelMesh) {
        for _ in 0..6 {
            voxel_mesh.push_uvs([0., 0.]);
            voxel_mesh.push_uvs([1., 1.]);
            voxel_mesh.push_uvs([0., 0.]);
            voxel_mesh.push_uvs([1., 1.]);
        }
    }
}
