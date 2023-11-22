import android.content.Context

object AppPreferences {
    private const val PREFS_NAME = "MyAppPreferences"
    private const val KEY_FIRST_LAUNCH = "isFirstLaunch"
    private const val KEY_INTRO_COMPLETED = "isIntroCompleted"

    fun isFirstLaunch(context: Context): Boolean {
        val pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return pref.getBoolean(KEY_FIRST_LAUNCH, true)
    }

    fun setFirstLaunch(context: Context, isFirstLaunch: Boolean) {
        val editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        editor.putBoolean(KEY_FIRST_LAUNCH, isFirstLaunch)
        editor.apply()
    }

    fun isIntroCompleted(context: Context): Boolean {
        val pref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return pref.getBoolean(KEY_INTRO_COMPLETED, false)
    }

    fun setIntroCompleted(context: Context, isCompleted: Boolean) {
        val editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        editor.putBoolean(KEY_INTRO_COMPLETED, isCompleted)
        editor.apply()
    }
}