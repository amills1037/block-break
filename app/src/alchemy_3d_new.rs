use bevy::prelude::*;
use bevy::tasks::{AsyncComputeTaskPool, Task, futures_lite::future};
#[derive(States, Debug, Clone, PartialEq, Eq, Hash, Default)]
pub enum GameState {
    #[default]
    Loading,
    InGame,
}

#[derive(Resource)]
struct LoadingProgress {
    total_chunks: usize,
    finished_chunks: usize,

    // A timer that must finish before we transition
    min_time: Timer,
}

impl Default for LoadingProgress {
    fn default() -> Self {
        Self {
            total_chunks: 0,
            finished_chunks: 0,
            // Set to 1 second plus, runs once
            min_time: Timer::from_seconds(10.0, TimerMode::Once),
        }
    }
}

#[derive(Component)]
struct ChunkTask(Task<String>);

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

fn start_loading_voxels(mut commands: Commands, mut progress: ResMut<LoadingProgress>) {
    let thread_poll = AsyncComputeTaskPool::get();
    let num_chunks = 10;
    progress.total_chunks = num_chunks;

    for i in 0..num_chunks {
        let task = thread_poll.spawn(async move {
            std::thread::sleep(std::time::Duration::from_millis(20));
            format!("Chunk {} Mesh", i)
        });

        commands.spawn((ChunkTask(task), OnLoadingScreen));
    }
}

fn check_loading_progress(
    mut commands: Commands,
    mut tasks: Query<(Entity, &mut ChunkTask)>,
    mut progress: ResMut<LoadingProgress>,
    mut next_state: ResMut<NextState<GameState>>,
) {
    for (entity, mut task) in &mut tasks {
        if let Some(task_result) = future::block_on(future::poll_once(&mut task.0)) {
            progress.finished_chunks += 1;

            info!(task_result);

            commands.entity(entity).remove::<ChunkTask>();
        }
    }

    let tasks_done = progress.finished_chunks >= progress.total_chunks && progress.total_chunks > 0;
    let time_done = progress.min_time.is_finished();

    if tasks_done && time_done {
        next_state.set(GameState::InGame);
    }
}

fn despawn_screen<T: Component>(to_despawn: Query<Entity, With<T>>, mut commands: Commands) {
    for entity in &to_despawn {
        // Use despawn_recursive to ensure children are also removed
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

pub struct Alchemy3DPlugin;

impl Plugin for Alchemy3DPlugin {
    fn build(&self, app: &mut App) {
        app.init_state::<GameState>()
            .init_resource::<LoadingProgress>()
            .add_systems(
                OnEnter(GameState::Loading),
                (setup_loading_ui, start_loading_voxels),
            )
            .add_systems(
                Update,
                (
                    tick_loading_timer,
                    check_loading_progress,
                    update_progress_bar,
                )
                    .run_if(in_state(GameState::Loading)),
            )
            .add_systems(
                OnExit(GameState::Loading),
                despawn_screen::<OnLoadingScreen>,
            );
    }
}
