use bevy::prelude::*;

use bevy::tasks::{AsyncComputeTaskPool, Task, futures_lite::future};

use crate::terrain::voxel_terrain_generator::TerrainGenerator;

use super::voxel_data::VoxelData;
use super::voxel_terrain_generator::HeightMapTerrainGenerator;

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
pub struct TerrainChunkGeneratring(Task<VoxelData>);

#[derive(Component)]
pub struct TerrainChunkGenerated;

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

        //         commands.spawn((ChunkTask(task), OnLoadingScreen));
        //remove GenerateTerrainChunk component and add TerrainChunkGeneratring component
        commands
            .entity(entity_id)
            .remove::<GenerateTerrainChunk>()
            .insert(TerrainChunkGeneratring(task));
    }
}

/**
 * Check if Task has completed
 */
fn terrain_chunks_generating(
    mut commands: Commands,
    query_chunks: Query<(Entity, &mut TerrainChunkGeneratring)>,
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
                .remove::<TerrainChunkGeneratring>()
                .insert(TerrainChunkGenerated);
        }
    }
}

// fn terrain_chunks_generated(
//     mut commands: Commands,
//     query_chunks: Query<(Entity, &TerrainChunkGenerated)>,
// ) {
//     for (entity_id, _) in query_chunks {
//         commands.entity(entity_id).remove::<TerrainChunkGenerated>();
//     }
// }

// fn generate_terrain_chunks(mut commands: Commands, mut coords: ResMut) {}

// #[derive(Component)]
// struct ChunkTask(Task<String>);

// fn check_loading_progress(
//     mut commands: Commands,
//     mut tasks: Query<(Entity, &TerrainChunkGenerated)>,
//     mut progress: ResMut<LoadingProgress>,
//     mut next_state: ResMut<NextState<AppState>>,
// ) {
//     for (entity, mut task) in &mut tasks {
//         if let Some(task_result) = future::block_on(future::poll_once(&mut task.0)) {
//             progress.finished_chunks += 1;

//             info!(task_result);

//             commands.entity(entity).remove::<ChunkTask>();
//         }
//     }

//     let tasks_done = progress.finished_chunks >= progress.total_chunks && progress.total_chunks > 0;
//     let time_done = progress.min_time.is_finished();

//     if tasks_done && time_done {
//         next_state.set(AppState::InGame);
//     }
// }

pub struct TerrainPlugin;

impl Plugin for TerrainPlugin {
    fn build(&self, app: &mut App) {
        app.add_systems(
            Update,
            (
                generate_terrain_chunks,
                terrain_chunks_generating,
                // terrain_chunks_generated,
            ),
        );
    }
}
