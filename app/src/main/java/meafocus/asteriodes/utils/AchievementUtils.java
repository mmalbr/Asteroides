package meafocus.asteriodes.utils;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import meafocus.asteriodes.data.WeaponData;
import meafocus.asteriodes.views.GameView;



public class AchievementUtils implements GameView.GameListener {

    private boolean isTutorial;
    private boolean isOutOfAmmo;

    private int asteroidsHit;
    private int asteroidsPassed;
    private long outOfAmmoTime;


    public AchievementUtils(Context context) {
    }

    @Override
    public void onStart(boolean isTutorial) {
        this.isTutorial = isTutorial;
        isOutOfAmmo = false;
        asteroidsHit = 0;
        asteroidsPassed = 0;
    }

    @Override
    public void onTutorialFinish() {
        isTutorial = false;
    }

    @Override
    public void onStop(int score) {
        isTutorial = false;

        if (score > 300)
            unlock("Texto1");
        else if (score > 200)
            unlock("Texto1");
        else if (score > 100)
            unlock("Texto1");
        else if (score > 50)
            unlock("Texto1");
    }

    @Override
    public void onAsteroidPassed() {
        if (!isTutorial) {
            if (asteroidsHit == 0 && asteroidsPassed == 0)
                unlock("Texto1");
            asteroidsPassed++;
        }
    }

    @Override
    public void onAsteroidCrashed() {
        if (!isTutorial && isOutOfAmmo && System.currentTimeMillis() - outOfAmmoTime < 3000)
            unlock("Texto1");
    }

    @Override
    public void onWeaponUpgraded(WeaponData weapon) {
        if (weapon.equals(WeaponData.WEAPONS[5]))
            unlock("Texto1");
        if (weapon.equals(WeaponData.WEAPONS[WeaponData.WEAPONS.length - 1]))
            unlock("Texto1");
    }

    @Override
    public void onAmmoReplenished() {
        if (!isTutorial) {
            if (isOutOfAmmo)
                unlock("Texto1");
            isOutOfAmmo = false;
        }
    }

    @Override
    public void onProjectileFired(WeaponData weapon) {

    }

    @Override
    public void onOutOfAmmo() {
        if (!isTutorial) {
            if (!isOutOfAmmo)
                outOfAmmoTime = System.currentTimeMillis();
            isOutOfAmmo = true;

            if (System.currentTimeMillis() - outOfAmmoTime > 3000)
                unlock("Texto1");
        }
    }

    @Override
    public void onAsteroidHit(int score) {
        if (!isTutorial && asteroidsHit == 0 && asteroidsPassed == 0)
            unlock("Texto1");
        asteroidsHit++;
    }

    private void unlock(String key) {
        /**  if (apiClient.isConnected())
         Games.Achievements.unlock(apiClient, context.getString(key)); **/
    }

}
