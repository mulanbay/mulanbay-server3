package cn.mulanbay.pms.web.bean.res.food;

import cn.mulanbay.pms.persistent.domain.Diet;
import cn.mulanbay.pms.persistent.domain.FoodEnergy;

/**
 * @author fenghong
 * @date 2025/3/11
 */
public class DietEnergyVo {

    private FoodEnergy energy;

    private Diet diet;

    public DietEnergyVo() {
    }

    public DietEnergyVo(FoodEnergy energy, Diet diet) {
        this.energy = energy;
        this.diet = diet;
    }

    public FoodEnergy getEnergy() {
        return energy;
    }

    public void setEnergy(FoodEnergy energy) {
        this.energy = energy;
    }

    public Diet getDiet() {
        return diet;
    }

    public void setDiet(Diet diet) {
        this.diet = diet;
    }
}
