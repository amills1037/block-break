use bevy::prelude::*;

#[derive(States, Debug, Clone, PartialEq, Eq, Hash, Default)]
pub enum AppState {
    // PreMenuLoading,
    // AppMenu,
    #[default]
    GameLoading, // Show loading screen here
    InGame, // Gameplay starts here
}

#[derive(States, Debug, Clone, PartialEq, Eq, Hash, Default)]
pub enum GameState {
    #[default]
    Playing,
    Paused,
}
