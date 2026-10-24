use std::sync::Arc;

use bevy::prelude::*;
use bevy::tasks::{AsyncComputeTaskPool, Task, futures_lite::future};

use crate::voxel_data::VoxelData;
use crate::voxel_terrain_generator::{HeightMapTerrainGenerator, TerrainGenerator};

#[derive(Component)]
pub struct ChunkCoords {
    coords: IVec3,
}

impl ChunkCoords {
    pub fn new(x: i32, y: i32, z: i32) -> Self {
        Self {
            coords: IVec3::new(x, y, z),
        }
    }
}

#[derive(Component)]
pub struct GenerateTerrainChunk;

#[derive(Component)]
pub struct TerrainChunkGenerating(Task<VoxelData>);

#[derive(Component)]
pub struct TerrainChunkGenerated {
    pub data: Arc<VoxelData>,
}

impl TerrainChunkGenerated {
    fn new(data: VoxelData) -> Self {
        Self {
            data: Arc::new(data),
        }
    }
}

/**
 * Create Task to generate height map
 */
fn generate_terrain_chunks(
    mut commands: Commands,
    query_chunks: Query<(Entity, &ChunkCoords), With<GenerateTerrainChunk>>,
) {
    let thread_poll = AsyncComputeTaskPool::get();

    for (entity_id, coords) in query_chunks {
        let coords = coords.coords;

        //spawn task to generate chunk here
        let task = thread_poll.spawn(async move {
            info!("generating: {}", coords);

            let mut data = VoxelData::new(coords);
            let generator = HeightMapTerrainGenerator::new();

            generator.generate(&mut data);

            data
            // std::thread::sleep(std::time::Duration::from_millis(5));
        });

        //remove GenerateTerrainChunk component and add TerrainChunkGeneratring component
        commands
            .entity(entity_id)
            .remove::<GenerateTerrainChunk>()
            .insert(TerrainChunkGenerating(task));
    }
}

/**
 * Check if Task has completed
 */
fn terrain_chunks_generating(
    mut commands: Commands,
    query_chunks: Query<(Entity, &mut TerrainChunkGenerating)>,
) {
    for (entity, mut task) in query_chunks {
        if let Some(task_result) = future::block_on(future::poll_once(&mut task.0)) {
            info!(
                "generated: {} data: {}",
                task_result.coords,
                task_result.get_data(
                    task_result.coords.x + 10,
                    task_result.coords.y + 10,
                    task_result.coords.z + 10
                )
            );

            commands
                .entity(entity)
                .remove::<TerrainChunkGenerating>()
                .insert(TerrainChunkGenerated::new(task_result));
        }
    }
}

pub struct TerrainPlugin;

impl Plugin for TerrainPlugin {
    fn build(&self, app: &mut App) {
        app.add_systems(
            Update,
            (
                generate_terrain_chunks,   //spawn tasks
                terrain_chunks_generating, //listen for completed tasks
            ),
        );
    }
}
