use wasm_bindgen::prelude::*;

// mod alchemy3_d;
// mod alchemy_3d_new;
// mod diagnostic;
// mod mesh_generator;
// mod noise; //::noise_generator;
mod game_loading;
mod mesh_generator;
mod noise;
mod state;
mod terrain;

use bevy::{
    // diagnostic::{
    //     EntityCountDiagnosticsPlugin, FrameTimeDiagnosticsPlugin, LogDiagnosticsPlugin,
    //     SystemInformationDiagnosticsPlugin,
    // },
    prelude::*,
    window::WindowPlugin, // render::diagnostic::RenderDiagnosticsPlugin,
};

use game_loading::GameLoadingPlugin;
use state::AppState;
use terrain::TerrainPlugin;

#[wasm_bindgen]
pub fn main() {
    App::new()
        .add_plugins(DefaultPlugins.set(WindowPlugin {
            primary_window: Some(Window {
                // Matches the id="my-canvas" in your HTML
                canvas: Some("#webasm-canvas".to_string()),
                fit_canvas_to_parent: true,
                ..default()
            }),
            ..default()
        })) //Default Bevy Plugins
        // .add_plugins((
        //     //Bevy Diagnostics Plugins
        //     // Adds frame time, FPS and frame count diagnostics.
        //     FrameTimeDiagnosticsPlugin::default(),
        //     // Adds an entity count diagnostic.
        //     EntityCountDiagnosticsPlugin::default(),
        //     // Adds cpu and memory usage diagnostics for systems and the entire game process.
        //     SystemInformationDiagnosticsPlugin::default(),
        //     // Forwards various diagnostics from the render app to the main app.
        //     // These are pretty verbose but can be useful to pinpoint performance issues.
        //     RenderDiagnosticsPlugin::default(),
        //     //Displays diagnostic information to the console
        //     LogDiagnosticsPlugin::default(),
        // ))
        //Alchemy 3D specific code
        .init_state::<AppState>() //init_state needs to be called after DefaultPlugin
        .add_plugins(GameLoadingPlugin)
        .add_plugins(TerrainPlugin)
        .add_systems(Startup, setup)
        .run();
}

// /// set up a simple 3D scene
fn setup(
    mut commands: Commands,
    mut meshes: ResMut<Assets<Mesh>>,
    mut materials: ResMut<Assets<StandardMaterial>>,
) {
    // circular base
    commands.spawn((
        Mesh3d(meshes.add(Circle::new(4.0))),
        MeshMaterial3d(materials.add(Color::WHITE)),
        Transform::from_rotation(Quat::from_rotation_x(-std::f32::consts::FRAC_PI_2)),
    ));
    // cube
    // commands.spawn((
    //     Mesh3d(meshes.add(Cuboid::new(1.0, 1.0, 1.0))),
    //     MeshMaterial3d(materials.add(Color::srgb_u8(124, 144, 255))),
    //     Transform::from_xyz(0.0, 0.5, 0.0),
    // ));
    // light
    commands.spawn((
        PointLight {
            shadows_enabled: true,
            ..default()
        },
        Transform::from_xyz(40.0, 80.0, 40.0),
    ));
    // camera
    commands.spawn((
        Camera3d::default(),
        Transform::from_xyz(-20.5, 40.5, 90.0).looking_at(Vec3::ZERO, Vec3::Y),
    ));
}
