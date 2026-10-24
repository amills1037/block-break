//!
//! Main WebAssembly library
//!
//!     main(canvas_id)
//!

use wasm_bindgen::prelude::*;

use bevy::prelude::*;

use app_state::AppState;
use game_loading::GameLoadingPlugin;
use mesh_generator::MeshGeneratorPlugin;
use terrain::TerrainPlugin;

#[wasm_bindgen]
pub fn main(canvas_id: &str) {
    App::new()
        .add_plugins(DefaultPlugins.set(WindowPlugin {
            primary_window: Some(Window {
                // Matches the id="my-canvas" in your HTML
                canvas: Some(format!("#{}", canvas_id)),
                fit_canvas_to_parent: true,
                // ?? Prevents standard canvas creation behavior from bypassing CSS rules
                prevent_default_event_handling: false,
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
        .add_plugins(MeshGeneratorPlugin)
        .add_plugins(TerrainPlugin)
        .add_systems(Startup, (setup, setup_ambient_light))
        .run();
}

// /// set up a simple 3D scene
fn setup(mut commands: Commands) {
    // light
    commands.spawn((
        PointLight {
            shadow_maps_enabled: true,
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

fn setup_ambient_light(mut ambient_light: ResMut<GlobalAmbientLight>) {
    ambient_light.brightness = 400.0;
}
