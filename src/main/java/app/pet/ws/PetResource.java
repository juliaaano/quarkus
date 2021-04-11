package app.pet.ws;

import app.pet.Pet;

public class PetResource {

    public String id;
    public String type;
    public String variety;
    public String denomination;

    Pet map() {
        return Pet.pet(this.id, this.type, this.variety, this.denomination);
    }
}
