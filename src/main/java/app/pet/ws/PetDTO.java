package app.pet.ws;

import app.pet.Pet;

public class PetDTO {

    private String id;
    private String type;
    private String variety;
    private String denomination;

    static PetDTO petDTO(final Pet pet) {

        final PetDTO petDTO = new PetDTO();

        petDTO.id = pet.getIdentifier();
        petDTO.type = pet.getSpecies();
        petDTO.variety = pet.getBreed();
        petDTO.denomination = pet.getName();

        return petDTO;
    }

    Pet map() {
        return Pet.pet(this.id, this.type, this.variety, this.denomination);
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getVariety() {
        return variety;
    }

    public String getDenomination() {
        return denomination;
    }
}
