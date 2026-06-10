use bevy::prelude::*;
// use bevy::tasks::{AsyncComputeTaskPool, Task, futures_lite::future};

use super::load_chunks::load_chunks;
use crate::{state::AppState, terrain::terrain_plugin::TerrainChunkGenerated};

const MIN_LOADING_PROGRESS_TIME: f32 = 2.0; //to short and the progress bar with flash on fast systems

#[derive(Resource)]
pub struct LoadingProgress {
    pub total_chunks: usize,
    pub finished_chunks: usize,

    // A timer that must finish before we transition
    min_time: Timer,
}

impl Default for LoadingProgress {
    fn default() -> Self {
        Self {
            total_chunks: 0,
            finished_chunks: 0,
            min_time: Timer::from_seconds(MIN_LOADING_PROGRESS_TIME, TimerMode::Once),
        }
    }
}

#[derive(Component)]
struct OnLoadingScreen;

#[derive(Component)]
struct ProgressBarFill;

fn setup_loading_ui(mut commands: Commands) {
    commands
        .spawn((
            Node {
                width: Val::Px(400.0),
                height: Val::Px(40.0),
                border: UiRect::all(Val::Px(2.0)),
                ..default()
            },
            BorderColor::all(Color::WHITE),
            OnLoadingScreen,
        ))
        .with_children(|parent| {
            parent.spawn((
                Node {
                    width: Val::Percent(10.0),
                    height: Val::Percent(100.0),
                    ..default()
                },
                BackgroundColor(Color::srgb(0.0, 1.0, 0.0)),
                ProgressBarFill,
            ));
        });
}

fn check_loading_progress(
    chunks: Query<Entity, With<TerrainChunkGenerated>>,
    mut progress: ResMut<LoadingProgress>,
    mut next_state: ResMut<NextState<AppState>>,
) {
    let count = chunks.count();

    progress.finished_chunks = count;

    let tasks_done = progress.finished_chunks >= progress.total_chunks && progress.total_chunks > 0;
    let time_done = progress.min_time.is_finished();

    if tasks_done && time_done {
        next_state.set(AppState::InGame);
    }
}

fn despawn_screen<T: Component>(to_despawn: Query<Entity, With<T>>, mut commands: Commands) {
    for entity in &to_despawn {
        commands.entity(entity).despawn();
    }
}

fn tick_loading_timer(time: Res<Time>, mut progress: ResMut<LoadingProgress>) {
    progress.min_time.tick(time.delta());
}

fn update_progress_bar(
    progress: Res<LoadingProgress>,
    mut query: Query<&mut Node, With<ProgressBarFill>>,
) {
    if let Ok(mut node) = query.single_mut() {
        let task_ratio = progress.finished_chunks as f32 / progress.total_chunks as f32;

        // We take the MINIMUM of the actual task progress
        // and the timer progress for a "controlled" fill
        let time_ratio = progress.min_time.fraction();
        let actual_ratio = task_ratio.min(time_ratio);

        node.width = Val::Percent(actual_ratio * 100.0);
    }
}

pub struct GameLoadingPlugin;

impl Plugin for GameLoadingPlugin {
    fn build(&self, app: &mut App) {
        app.init_resource::<LoadingProgress>()
            .add_systems(
                OnEnter(AppState::GameLoading),
                (setup_loading_ui, load_chunks),
            )
            .add_systems(
                Update,
                (
                    tick_loading_timer,
                    check_loading_progress,
                    update_progress_bar,
                )
                    .run_if(in_state(AppState::GameLoading)),
            )
            .add_systems(
                OnExit(AppState::GameLoading),
                despawn_screen::<OnLoadingScreen>,
            );
    }
}
