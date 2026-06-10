pub struct VoxelMesh {
    pub vertices: Vec<[f32; 3]>,
    pub triangles: Vec<u32>,
    pub normals: Vec<[f32; 3]>,
    pub uvs: Vec<[f32; 2]>,
}

impl VoxelMesh {
    pub fn new() -> Self {
        VoxelMesh {
            vertices: Vec::with_capacity(324000),
            triangles: Vec::with_capacity(486000),
            normals: Vec::with_capacity(324000),
            uvs: Vec::with_capacity(324000),
        }
    }
}

impl VoxelMesh {
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
