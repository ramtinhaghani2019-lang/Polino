package ir.polino.app

import android.app.Application
import ir.polino.app.data.PolinoDatabase

class PolinoApp : Application() {
    val database by lazy { PolinoDatabase.get(this) }
}
