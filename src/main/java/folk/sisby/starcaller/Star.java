package folk.sisby.starcaller;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;

public class Star {
    public final Vec3d pos;
    public long groundedTick;
    public int color;
    public String editor; // Unused on client
    public int editorColor; // Unused on client

    public Star(Vec3d pos) {
        this.pos = pos;
        readData(null, null, null, null);
    }

    public void readData(Long groundedTick, Integer color, String editor, Integer editorColor) {
        this.groundedTick = groundedTick != null ? groundedTick : -1;
        this.color = color != null ? color : 0xFFFFFF;
        this.editor = editor;
        this.editorColor = editorColor != null ? editorColor : 0xFFFFFF;
    }

    public void readNbt(NbtCompound nbt) {
        readData(
                nbt.contains("groundedTick") ? nbt.getLong("groundedTick") : null,
                nbt.contains("color") ? nbt.getInt("color") : null,
                nbt.contains("editor") ? nbt.getString("editor") : null,
                nbt.contains("color") ? nbt.getInt("color") : null
        );
    }

    public NbtCompound toNbt() {
        NbtCompound starCompound = new NbtCompound();
        if (groundedTick != -1) starCompound.putLong("groundedTick", groundedTick);
        if (color != 0xFFFFFF) starCompound.putInt("color", color);
        if (editor != null) starCompound.putString("editor", editor);
        if (editorColor != 0xFFFFFF) starCompound.putInt("color", color);
        return starCompound;
    }

    @Override
    public String toString() {
        return "%s, %s, %s".formatted(Math.round(pos.x * 100) / 100.0F, Math.round(pos.y * 100) / 100.0F, Math.round(pos.z * 100) / 100.0F);
    }
}
