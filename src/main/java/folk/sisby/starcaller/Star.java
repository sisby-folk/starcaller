package folk.sisby.starcaller;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;

public class Star {
    public static final int DEFAULT_COLOR = 0xFFFFFFFF;
    public static final int DEFAULT_EDITOR_COLOR = 0xFFFFFF;
    public static final long DEFAULT_GROUNDED_TICK = -1;
    public static final String KEY_GROUNDED_TICK = "groundedTick";
    public static final String KEY_COLOR = "color";
    public static final String KEY_EDITOR = "editor";
    public static final String KEY_EDITOR_COLOR = "groundedTick";

    public final Vec3d pos;
    public long groundedTick;
    public int color; // With Alpha
    public String editor; // Unused on client
    public int editorColor; // Unused on client

    public Star(Vec3d pos) {
        this.pos = pos;
        readData(null, null, null, null);
    }

    public void readData(Long groundedTick, Integer color, String editor, Integer editorColor) {
        this.groundedTick = groundedTick != null ? groundedTick : DEFAULT_GROUNDED_TICK;
        this.color = color != null ? color : DEFAULT_COLOR;
        this.editor = editor;
        this.editorColor = editorColor != null ? editorColor : DEFAULT_EDITOR_COLOR;
    }

    public void readNbt(NbtCompound nbt) {
        readData(
                nbt.contains(KEY_GROUNDED_TICK) ? nbt.getLong(KEY_GROUNDED_TICK) : null,
                nbt.contains(KEY_COLOR) ? nbt.getInt(KEY_COLOR) : null,
                nbt.contains(KEY_EDITOR) ? nbt.getString(KEY_EDITOR) : null,
                nbt.contains(KEY_EDITOR_COLOR) ? nbt.getInt(KEY_EDITOR_COLOR) : null
        );
    }

    public NbtCompound toNbt() {
        NbtCompound starCompound = new NbtCompound();
        if (groundedTick != DEFAULT_GROUNDED_TICK) starCompound.putLong(KEY_GROUNDED_TICK, groundedTick);
        if (color != DEFAULT_COLOR) starCompound.putInt(KEY_COLOR, color);
        if (editor != null) starCompound.putString(KEY_EDITOR, editor);
        if (editorColor != DEFAULT_EDITOR_COLOR) starCompound.putInt(KEY_EDITOR_COLOR, color);
        return starCompound;
    }

    @Override
    public String toString() {
        return "%s, %s, %s".formatted(Math.round(pos.x * 100) / 100.0F, Math.round(pos.y * 100) / 100.0F, Math.round(pos.z * 100) / 100.0F);
    }
}
