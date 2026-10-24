pub struct VoxelChunkMesh {
    pub vertices: Vec<[f32; 3]>,
    pub triangles: Vec<u32>,
    pub normals: Vec<[f32; 3]>,
    pub uvs: Vec<[f32; 2]>,

    pub x_coord: i32,
    pub y_coord: i32,
    pub z_coord: i32,
}

impl VoxelChunkMesh {
    pub fn new(x: i32, y: i32, z: i32) -> Self {
        VoxelChunkMesh {
            vertices: Vec::with_capacity(324_000),
            triangles: Vec::with_capacity(486_000),
            normals: Vec::with_capacity(324_000),
            uvs: Vec::with_capacity(324_000),
            x_coord: x,
            y_coord: y,
            z_coord: z,
        }
    }
}

impl VoxelChunkMesh {
    pub fn push_vertex(&mut self, vertex: [f32; 3]) {
        self.vertices.push(vertex);
    }

    pub fn push_triangles(&mut self, indexes: [u32; 3]) {
        self.triangles.push(indexes[0]);
        self.triangles.push(indexes[1]);
        self.triangles.push(indexes[2]);
    }

    pub fn push_normals(&mut self, normal: [f32; 3]) {
        self.normals.push(normal);
    }

    pub fn push_uvs(&mut self, uv: [f32; 2]) {
        self.uvs.push(uv);
    }
}
