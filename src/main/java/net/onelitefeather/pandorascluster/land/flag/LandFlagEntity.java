package net.onelitefeather.pandorascluster.land.flag;

import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Entity
public class LandFlagEntity {

    @Id
    @Column
    private String name;

    @Column
    private String value;

    @Column
    private byte type;

    @Enumerated(EnumType.STRING)
    private LandFlagType flagType;

    public LandFlagEntity() {
    }

    public LandFlagEntity(@NotNull String name, @NotNull String value, byte type, @NotNull LandFlagType flagType) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.flagType = flagType;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public <T> T getValue() {
        var result = switch(this.type) {
            case 0 -> this.value;
            case 1 -> Integer.getInteger(this.value);
            case 2 -> Boolean.getBoolean(this.value);
            case 3 -> Double.parseDouble(this.value);
            case 4 -> Float.parseFloat(this.value);
            case 5 -> Short.parseShort(this.value);
            case 6 -> Byte.parseByte(this.value);
            default -> null;
        };
        return (T) result;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    @Nullable
    public LandFlag getFlag() {
        return LandFlag.findByName(this.name);
    }

    public void setValue(@NotNull String value) {
        this.value = value;
    }

    @NotNull
    public LandFlagType getFlagType() {
        return flagType;
    }

    public void setFlagType(@NotNull LandFlagType flagType) {
        this.flagType = flagType;
    }

    public static final class Builder {

        private String name;
        private String value;
        
        private LandFlagType flagType;
        private byte type;

        public LandFlagEntity.Builder name(@NotNull String name) {
            this.name = name;
            return this;
        }

        public LandFlagEntity.Builder withType(@NotNull LandFlagType flagType) {
            this.flagType = flagType;
            return this;
        }

        public LandFlagEntity.Builder type(byte type) {
            this.type = type;
            return this;
        }

        public LandFlagEntity.Builder value(@NotNull String value) {
            this.value = value.trim();
            return this;
        }

        public LandFlagEntity build() {
            return new LandFlagEntity(this.name, this.value, this.type, this.flagType);
        }
    }

}
