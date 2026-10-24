use bevy::prelude::*;

use terrain::terrain_plugin::{ChunkCoords, GenerateTerrainChunk};

use super::loading_plugin::LoadingProgress;

pub fn load_chunks(mut commands: Commands, mut progress: ResMut<LoadingProgress>) {
    // progress.total_chunks = 11 * 11 * 11;

    for z in -5..6 {
        // for y in -2..3 {
        for x in -5..6 {
            progress.total_chunks += 1;
            commands.spawn((GenerateTerrainChunk, ChunkCoords::new(x, 0, z)));
        }
    }
    // }
}
