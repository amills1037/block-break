use std::sync::Arc;

use bevy::{
    asset::RenderAssetUsages,
    // diagnostic::{
    //     EntityCountDiagnosticsPlugin, FrameTimeDiagnosticsPlugin, LogDiagnosticsPlugin,
    //     SystemInformationDiagnosticsPlugin,
    // },
    mesh::{Indices, PrimitiveTopology},
    prelude::*,
};

use bevy::tasks::{AsyncComputeTaskPool, Task, futures_lite::future};

use crate::mesh_generator::MeshGenerator;
use crate::naive::NaiveMeshGenerator;
use crate::voxel_chunk_mesh::VoxelChunkMesh;
use terrain::terrain_plugin::TerrainChunkGenerated;

#[derive(Component)]
pub struct GenerateMesh;

#[derive(Component)]
pub struct MeshGenerating(Task<VoxelChunkMesh>);

#[derive(Component)]
pub struct MeshGenerated;

fn generate_mesh(mut commands: Commands, query_chunks: Query<(Entity, &TerrainChunkGenerated)>) {
    let thread_poll = AsyncComputeTaskPool::get();

    for (entity_id, voxel_data) in query_chunks {
        let data = Arc::clone(&voxel_data.data);
        //spawn task to generate chunk here
        let task: Task<VoxelChunkMesh> = thread_poll.spawn(async move {
            let mut mesh = VoxelChunkMesh::new(data.coords.x, data.coords.y, data.coords.z);

            NaiveMeshGenerator::generate_mesh(&data, &mut mesh);

            mesh
        });

        //remove GenerateTerrainChunk component and add TerrainChunkGeneratring component
        commands
            .entity(entity_id)
            .remove::<TerrainChunkGenerated>()
            .insert(MeshGenerating(task));
    }
}

/**
 * Check if Task has completed
 */
fn mesh_generating(
    mut commands: Commands,
    mut meshes: ResMut<Assets<Mesh>>,
    mut materials: ResMut<Assets<StandardMaterial>>,
    query_chunks: Query<(Entity, &mut MeshGenerating)>,
) {
    for (entity, mut task) in query_chunks {
        if let Some(task_result) = future::block_on(future::poll_once(&mut task.0)) {
            //send meshes to render

            if task_result.vertices.len() > 0 {
                let mesh = Mesh::new(
                    PrimitiveTopology::TriangleList,
                    RenderAssetUsages::default(),
                )
                // Add 4 vertices, each with its own position attribute (coordinate in
                // 3D space), for each of the corners of the parallelogram.
                .with_inserted_attribute(Mesh::ATTRIBUTE_POSITION, task_result.vertices)
                // Assign a UV coordinate to each vertex.
                .with_inserted_attribute(Mesh::ATTRIBUTE_UV_0, task_result.uvs)
                // Assign normals (everything points outwards)
                .with_inserted_attribute(Mesh::ATTRIBUTE_NORMAL, task_result.normals)
                // After defining all the vertices and their attributes, build each triangle using the
                // indices of the vertices that make it up in a counter-clockwise order.
                .with_inserted_indices(Indices::U32(task_result.triangles));

                commands.spawn((
                    Mesh3d(meshes.add(mesh)),
                    MeshMaterial3d(materials.add(Color::srgb_u8(124, 255, 144))),
                    Transform::from_xyz(
                        (task_result.x_coord * terrain::voxel_data::CHUNK_Z_LENGTH) as f32,
                        (task_result.y_coord * terrain::voxel_data::CHUNK_Y_LENGTH) as f32,
                        (task_result.z_coord * terrain::voxel_data::CHUNK_Z_LENGTH) as f32,
                    ), //Transform::from_rotation(Quat::from_rotation_x(-std::f32::consts::FRAC_PI_2)),
                ));
            }

            commands
                .entity(entity)
                .remove::<MeshGenerating>()
                .insert(MeshGenerated);
        }
    }
}

pub struct MeshGeneratorPlugin;

impl Plugin for MeshGeneratorPlugin {
    fn build(&self, app: &mut App) {
        app.add_systems(
            Update,
            (
                generate_mesh, //spawn tasks
                mesh_generating, //listen for completed tasks
                               //mesh_generated,
            ),
        );
    }
}
