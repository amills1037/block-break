use bevy::asset::RenderAssetUsages;
use bevy::mesh::{Indices, Mesh, PrimitiveTopology};
use bevy::prelude::*;

use std::time::Instant;

// use crate::mesh_generator::mesh_cube::VoxelMeshCube;
use crate::noise::noise_generator::NoiseGenerator;
use crate::terrain::{
    voxel_data::VoxelData,
    voxel_terrain_generator::{FlatTerrainGenerator, HeightMapTerrainGenerator, TerrainGenerator},
};

use crate::mesh_generator::{
    MeshGenerator, greedy::GreedyMeshGenerator, marching_cubes::MarchingCubesMeshGenerator,
    naive::NaiveMeshGenerator, voxel_mesh::VoxelMesh,
};

pub struct Alchemy3DPlugin;

impl Plugin for Alchemy3DPlugin {
    fn build(&self, app: &mut App) {
        // add things to your app here
        app.add_systems(Startup, voxel_data);
    }
}

fn voxel_data(
    mut commands: Commands,
    mut meshes: ResMut<Assets<Mesh>>,
    mut materials: ResMut<Assets<StandardMaterial>>,
) {
    info!("Bevy voxel_data");

    let mut voxel_data = VoxelData::new(IVec3::new(0, 0, 0));
    let hm_terrain_generator = HeightMapTerrainGenerator::new();
    let flat_terrain_generator = FlatTerrainGenerator::new(15);

    for _ in 0..10 {
        let mut voxel_data_b = VoxelData::new(IVec3::new(0, 0, 0));
        let now = Instant::now();
        flat_terrain_generator.generate(&mut voxel_data_b);
        let diff = now.elapsed().as_secs_f64();
        info!("voxel_data generate flat: {:.3}µs", diff * 1_000_000.0);
    }

    let now = Instant::now();
    hm_terrain_generator.generate(&mut voxel_data);
    let diff = now.elapsed().as_secs_f64();
    info!("voxel_data generate heightmap: {:.3}µs", diff * 1_000_000.0);

    let mut voxel_mesh = VoxelMesh::new();
    // let greedy_mesh_generator = HeightMapTerrainGenerator::new();
    // let margin_cubes_generator = HeightMapTerrainGenerator::new();

    let now = Instant::now();
    // GreedyMeshGenerator::generate_mesh(&voxel_data, &mut voxel_mesh);
    MarchingCubesMeshGenerator::generate_mesh(&voxel_data, &mut voxel_mesh);
    // NaiveMeshGenerator::generate_mesh(&voxel_data, &mut voxel_mesh);
    let diff = now.elapsed().as_secs_f64();

    info!("voxel_mesh generate mesh: {:.3}µs", diff * 1_000_000.0);

    //32 * 32 * 15 = 15,360
    //
    info!(
        "voxel_mesh vertices: {} triangles: {} normals: {} uv: {}",
        voxel_mesh.vertices.len(),
        voxel_mesh.triangles.len(),
        voxel_mesh.normals.len(),
        voxel_mesh.uvs.len()
    );

    // let mut voxel_mesh = VoxelMesh::new();

    // VoxelMeshCube::push_vertices(&mut voxel_mesh, [0., 0., 0.]);
    // VoxelMeshCube::push_uvs(&mut voxel_mesh);
    // VoxelMeshCube::push_normals(&mut voxel_mesh);
    // VoxelMeshCube::push_triangles(&mut voxel_mesh, 0);

    let mesh = Mesh::new(
        PrimitiveTopology::TriangleList,
        RenderAssetUsages::default(),
    )
    // Add 4 vertices, each with its own position attribute (coordinate in
    // 3D space), for each of the corners of the parallelogram.
    .with_inserted_attribute(Mesh::ATTRIBUTE_POSITION, voxel_mesh.vertices)
    // Assign a UV coordinate to each vertex.
    .with_inserted_attribute(Mesh::ATTRIBUTE_UV_0, voxel_mesh.uvs)
    // Assign normals (everything points outwards)
    .with_inserted_attribute(Mesh::ATTRIBUTE_NORMAL, voxel_mesh.normals)
    // After defining all the vertices and their attributes, build each triangle using the
    // indices of the vertices that make it up in a counter-clockwise order.
    .with_inserted_indices(Indices::U32(voxel_mesh.triangles));

    commands.spawn((
        Mesh3d(meshes.add(mesh)),
        MeshMaterial3d(materials.add(Color::srgb_u8(224, 144, 255))),
        Transform::from_xyz(0., 0.5, 0.),
    ));

    let mut height_map: Vec<f32> = vec![0.; 16];
    let noise_generator = NoiseGenerator::new(2345);
    noise_generator.generate_noise_map((0., 0.), &mut height_map, 4, 4);

    for (i, f) in height_map.iter().enumerate() {
        info!("height_map[{}]: {} ", i, f);
    }
}
