use bevy::prelude::IVec3;

const CHUNK_LENGTH: i32 = 30;
pub const CHUNK_X_LENGTH: i32 = CHUNK_LENGTH;
pub const CHUNK_Y_LENGTH: i32 = CHUNK_LENGTH;
pub const CHUNK_Z_LENGTH: i32 = CHUNK_LENGTH;

const DATA_LENGTH: i32 = 32;
pub const DATA_Y_LENGTH: i32 = DATA_LENGTH;
pub const DATA_X_LENGTH: i32 = DATA_LENGTH;
pub const DATA_Z_LENGTH: i32 = DATA_LENGTH;

pub const DATA_XY_LENGTH: i32 = DATA_X_LENGTH * DATA_Y_LENGTH;
pub const DATA_XZ_LENGTH: i32 = DATA_X_LENGTH * DATA_Z_LENGTH;
pub const DATA_XYZ_LENGTH: i32 = DATA_X_LENGTH * DATA_Y_LENGTH * DATA_Z_LENGTH;

pub const EMPTY_VOXEL: u8 = 0;
pub const SOLID_VOXEL: u8 = 1;

#[macro_export]
macro_rules! index_offset {
    ($x:expr, $y:expr, $z:expr) => {
        (($z as i32) * DATA_XY_LENGTH + ($y as i32) * DATA_X_LENGTH + ($x as i32)) as usize
    };
}

#[macro_export]
macro_rules! index_offset_y {
    ($x:expr, $z:expr) => {
        (($z as i32) * DATA_X_LENGTH + ($x as i32)) as usize
    };
}

pub struct VoxelData {
    pub coords: IVec3,
    pub overlap: IVec3,

    blocks: [u8; DATA_XYZ_LENGTH as usize],
    block_mask_y: [u32; DATA_XZ_LENGTH as usize],
}

impl VoxelData {
    pub fn new(coords: IVec3) -> Self {
        let overlap = IVec3::ZERO;

        let blocks = [0 as u8; DATA_XYZ_LENGTH as usize];
        let block_mask_y = [0 as u32; DATA_XZ_LENGTH as usize];

        Self {
            coords,
            overlap,
            blocks,
            block_mask_y,
        }
    }

    pub fn new_with_overlap(coords: IVec3, overlap: IVec3) -> Self {
        let blocks = [0 as u8; DATA_XYZ_LENGTH as usize];
        let block_mask_y = [0 as u32; DATA_XZ_LENGTH as usize];

        Self {
            coords,
            overlap,
            blocks,
            block_mask_y,
        }
    }
}

impl Default for VoxelData {
    fn default() -> Self {
        let coords = IVec3::ZERO;
        let overlap = IVec3::ZERO;
        let blocks = [0 as u8; DATA_XYZ_LENGTH as usize];
        let block_mask_y = [0 as u32; DATA_XZ_LENGTH as usize];

        Self {
            coords,
            overlap,
            blocks,
            block_mask_y,
        }
    }
}

impl VoxelData {
    pub fn reset(&mut self, coords: IVec3) {
        self.coords = coords;
    }

    pub fn get_data(&self, x: i32, y: i32, z: i32) -> u8 {
        self.blocks[index_offset!(x, y, z)]
    }

    pub fn set_data(&mut self, x: i32, y: i32, z: i32, b: u8) {
        self.blocks[index_offset!(x, y, z)] = b;
        self.set_mask(x, y, z, b != EMPTY_VOXEL);
    }

    pub fn set_mask(&mut self, x: i32, y: i32, z: i32, b: bool) {
        if b {
            //set bit
            self.block_mask_y[index_offset_y!(x, z)] |= 1u32 << y;
        } else {
            //unset bit
            self.block_mask_y[index_offset_y!(x, z)] &= 0xFFFF_FFFF ^ (1u32 << y);
        }
    }

    pub fn get_mask(&self, x: i32, z: i32) -> u32 {
        self.block_mask_y[index_offset_y!(x, z)]
    }

    //     public bool GetBitValue(int x, int y, int z)
    //     {
    //         return (_blockMaskY[IndexOffsetY(x, z)] & (1U << y)) != 0U;
    //     }
}

//     public void SetMask(int x, int y, int z, bool b)
//     {
//         int pos = IndexOffsetY(x, z);
//         if (b)
//         {
//             //set bit
//             _blockMaskY[pos] |= (1U << y);
//         }
//         else
//         {
//             //unset bit
//             _blockMaskY[pos] &= (0xFFFF_FFFFU ^ (1U << y));
//         }
//     }

//     public uint GetMaskY(int x, int z)
//     {
//         return _blockMaskY[IndexOffsetY(x, z)];
//     }

//     public bool GetBitValue(int x, int y, int z)
//     {
//         return (_blockMaskY[IndexOffsetY(x, z)] & (1U << y)) != 0U;
//     }

//     [BurstCompile]
//     public static int IndexOffset(int x, int y, int z)
//     {
//         return z * DataXYLength + y * DataXLength + x;
//     }

//     [BurstCompile]
//     public static int IndexOffsetY(int x, int z)
//     {
//         return z * DataXLength + x;
//     }
// }
