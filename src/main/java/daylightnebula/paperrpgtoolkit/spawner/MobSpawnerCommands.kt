package daylightnebula.paperrpgtoolkit.spawner

import daylightnebula.paperrpgtoolkit.entities.CustomMob
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import kotlin.math.pow

class CreateMobSpawner: CommandExecutor {
    // /createmobspawner <custom mob id or entity type> <radius> [<x> <y> <z>] [<min children> <max children>] [<min time between spawn> <max time between spawn>]
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender !is Player) return false
        if (!sender.isOp) return false

        // send args back if necessary
        if (args == null || (args.size != 2 && args.size != 5 && args.size != 7 && args.size != 9)) {
            sender.sendMessage("/createmobspawner <custom mob id or entity type> <radius> [<x> <y> <z>] [<min children> <max children>] [<min time between spawn> <max time between spawn>]")
            return true
        }

        // try to get custom mob reference or entity type
        val customMob = CustomMob.mobs.firstOrNull { it.id.equals(args.first(), ignoreCase = true) }
        val entityType = EntityType.values().firstOrNull { it.name.equals(args.first(), ignoreCase = true) }
        if (customMob == null && entityType == null) {
            sender.sendMessage("Could not convert the first argument to a custom mob reference or a entity type")
            return true
        }

        // try to get radius
        val radius = args[1].toFloatOrNull()
        if (radius == null) {
            sender.sendMessage("Could not convert given radius to a float value")
            return true
        }

        // get location
        val location = Location(
            sender.world,
            tryToGetArgumentAsDouble(args, 2) ?: sender.location.x,
            tryToGetArgumentAsDouble(args, 3) ?: sender.location.y,
            tryToGetArgumentAsDouble(args, 4) ?: sender.location.z,
        )

        // get min and max children
        val minChildren = tryToGetArgumentAsInt(args, 5) ?: 0
        val maxChildren = tryToGetArgumentAsInt(args, 6) ?: 10

        // get min and max ticks between spawn
        val minTicksBetweenSpawn = tryToGetArgumentAsInt(args, 7) ?: 100
        val maxTicksBetweenSpawn = tryToGetArgumentAsInt(args, 8) ?: 500

        // create spawner
        MobSpawner(location, entityType?.name ?: "", radius, minTicksBetweenSpawn, maxTicksBetweenSpawn, minChildren, maxChildren)

        // success
        return true
    }

    private fun tryToGetArgumentAsDouble(args: Array<out String>, index: Int): Double? {
        if (index < 0 || index >= args.size) return null
        return args[index].toDoubleOrNull()
    }

    private fun tryToGetArgumentAsInt(args: Array<out String>, index: Int): Int? {
        if (index < 0 || index >= args.size) return null
        return args[index].toIntOrNull()
    }
}
class RemoveNearbySpawners: CommandExecutor {
    // /removenearbyspawners <radius>
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender !is Player) return false
        if (!sender.isOp) return false

        if (args == null || args.isEmpty()) {
            sender.sendMessage("/removenearbyspawners <radius>")
            return true
        }

        val radius = args.first().toFloatOrNull()
        if (radius == null) {
            sender.sendMessage("Could not convert radius to a float value")
            return true
        }

        val radiusSq = radius.pow(2f)
        val toRemove = MobSpawner.activeSpawners.filter { it.rootLocation.distanceSquared(sender.location) < radiusSq }
        toRemove.forEach { it.removeActiveEntities() }
        MobSpawner.activeSpawners.removeAll(toRemove)

        return true
    }
}