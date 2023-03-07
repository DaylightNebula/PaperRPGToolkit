package daylightnebula.paperrpgtoolkit.actions

import daylightnebula.paperrpgtoolkit.entities.CustomMob
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random

class ActionSpawnMob(
    private val id: String,
    private val location: Location?,
    private val spawnSpread: Vector,
    private val amount: Int
): Action() {
    constructor(json: JSONObject): this(
        json.getString("id"),
        json.optJSONArray("location"),
        json.optInt("world", 0),
        json.optJSONArray("spawnSpread"),
        json.optInt("amount", 1)
    )

    constructor(
        id: String,
        locationArr: JSONArray?,
        world: Int,
        spawnSpread: JSONArray?,
        amount: Int
    ): this(
        id,
        if (locationArr != null)
            Location(
                Bukkit.getWorlds()[world],
                locationArr.getDouble(0),
                locationArr.getDouble(1),
                locationArr.getDouble(2),
            )
        else null,
        if (spawnSpread != null)
            Vector(
                spawnSpread.getDouble(0),
                spawnSpread.getDouble(1),
                spawnSpread.getDouble(2),
            )
        else Vector(0.0, 0.0, 0.0),
        amount
    )

    override fun run(player: Player) {
        // try to find a custom move for the given ID
        val cMob = CustomMob.mobs[id]

        val location = this.location ?: player.location

        if (cMob != null) {
            // create custom mob with amount and location and spawn spread given
            for (i in 0 until amount) {
                cMob.spawnEntityAtLocation(
                    location.clone().add(
                        spawnSpread
                            .clone()
                            .multiply(
                                Vector(
                                    Random.nextFloat(),
                                    Random.nextFloat(),
                                    Random.nextFloat()
                                )
                            )
                    ), false
                )
            }
        }
        // otherwise, attempt to spawn via entity type, if no entity type, throw error
        else {
            val type = EntityType.values().firstOrNull { it.name.equals(id, true) }
                ?: throw IllegalArgumentException("Not custom mob or entity type with id $id")
            for (i in 0 until amount) {
                location.world.spawnEntity(
                    location.clone().add(
                        spawnSpread
                            .clone()
                            .multiply(
                                Vector(
                                    Random.nextFloat(),
                                    Random.nextFloat(),
                                    Random.nextFloat()
                                )
                            )
                    ),
                    type
                )
            }
        }
    }
}