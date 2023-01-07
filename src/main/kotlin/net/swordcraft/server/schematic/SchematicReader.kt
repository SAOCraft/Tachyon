package net.swordcraft.server.schematic

import net.minestom.server.command.builder.arguments.minecraft.ArgumentBlockState
import net.minestom.server.coordinate.Vec
import net.minestom.server.instance.block.Block
import net.swordcraft.server.Tachyon
import org.jglrxavpok.hephaistos.collections.ImmutableByteArray
import org.jglrxavpok.hephaistos.nbt.CompressedProcesser
import org.jglrxavpok.hephaistos.nbt.NBTCompound
import org.jglrxavpok.hephaistos.nbt.NBTInt
import org.jglrxavpok.hephaistos.nbt.NBTReader
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.file.Path

/* Helper object to parse schematics from files */
object SchematicReader {

    fun fromSchematicsFolder(fileName: String): Schematic {
        val file = File(Tachyon.schematicsFolder, "$fileName.schem")
        return fromFile(file)
    }

    fun fromFile(file: File): Schematic {
        return read(file.inputStream())
    }

    fun read(stream: InputStream): Schematic {
        val reader = NBTReader(stream, CompressedProcesser.GZIP)
        return read(reader)
    }

    fun read(path: Path): Schematic {
        val reader = NBTReader(path, CompressedProcesser.GZIP)
        return read(reader)
    }

    fun read(reader: NBTReader): Schematic {
        val tag: NBTCompound = reader.read() as NBTCompound

        val width: Short = tag.getShort("Width") ?: throw IOException("Schematic file is missing the width tag")
        val height: Short = tag.getShort("Height") ?: throw IOException("Schematic file is missing the height tag")
        val length: Short = tag.getShort("Length") ?: throw IOException("Schematic file is missing the length tag")

        val metadata: NBTCompound = tag.getCompound("Metadata") ?: throw IOException("Schematic file is missing the metadata tag")

        val offsetX: Int = metadata.getInt("WEOffsetX") ?: throw IOException("Schematic file is missing the WEOffsetX tag")
        val offsetY: Int = metadata.getInt("WEOffsetY") ?: throw IOException("Schematic file is missing the WEOffsetY tag")
        val offsetZ: Int = metadata.getInt("WEOffsetZ") ?: throw IOException("Schematic file is missing the WEOffsetZ tag")

        val palette: NBTCompound = tag.getCompound("Palette") ?: throw IOException("Schematic file is missing the palette tag")
        val blocksArray: ImmutableByteArray = tag.getByteArray("BlockData") ?: throw IOException("Schematic file is missing the block data tag")

        val paletteSize: Int = tag.getInt("PaletteMax") ?: throw IOException("Schematic file is missing the palette max tag")

        val blocks: Array<Block?> = arrayOfNulls(paletteSize)
        val state: ArgumentBlockState = ArgumentBlockState("")
        palette.forEach {
            val assigned: Int = (it.value as NBTInt).getValue();
            val block: Block = state.parse(it.key)
            blocks[assigned] = block
        }
        return Schematic(
            Vec(width.toDouble(), height.toDouble(), length.toDouble()),
            Vec(offsetX.toDouble(), offsetY.toDouble(), offsetZ.toDouble()),
            blocks,
            blocksArray.copyArray()
        )
    }



}