package com.svalero.protectoraAnimales.animalTests;

import com.svalero.protectoraAnimales.domain.Animal;
import com.svalero.protectoraAnimales.domain.Location;
import com.svalero.protectoraAnimales.domain.dto.animal.AnimalInDTO;
import com.svalero.protectoraAnimales.domain.dto.animal.AnimalOutDTO;
import com.svalero.protectoraAnimales.exception.runtime.ResourceNotFoundException;
import com.svalero.protectoraAnimales.repository.AnimalRepository;
import com.svalero.protectoraAnimales.repository.LocationRepository;
import com.svalero.protectoraAnimales.service.AnimalService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnimalServiceTests {

	@InjectMocks
	private AnimalService animalService;

	@Mock
	private AnimalRepository animalRepository;

	@Mock
	private LocationRepository locationRepository;

	@Mock
	private ModelMapper modelMapper;

	//region GET

	@Test
	public void testFindAnimalById() {
		long animalId = 2;
		Animal mockAnimal = new Animal(2, LocalDate.of(2021, 11, 15), "Nala", "Gato", 4, "Persa", "Mediano", true, false, 120.0f, "Juguetona y tranquila", new Location(), List.of());

		when(animalRepository.findById(animalId)).thenReturn(Optional.ofNullable(mockAnimal));

		Animal result = animalService.findById(animalId);

		assertEquals("Nala", result.getName());
		assertEquals(LocalDate.of(2021, 11, 15), result.getIncorporationDate());
		assertEquals("Persa", result.getBreed());

		verify(animalRepository, times(1)).findById(animalId);
	}

	@Test
	public void testFindAnimalWhenAnimalIsNotFound() {
		long animalId = 35;

		when(animalRepository.findById(animalId)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> animalService.findById(animalId));

		verify(animalRepository, times(1)).findById(animalId);
	}

	@Test
	public void testGetAnimals() {
		List<Animal> mockAnimalList = List.of(
				new Animal(1, LocalDate.of(2023, 1, 10), "Luna", "Perro", 3, "Labrador", "Grande", true, false, 150.0f, "Muy amigable", new Location(), List.of()),
				new Animal(2, LocalDate.now(), "Milo", "Gato", 2, "Siamés", "Pequeño", false, true, 100.0f, "Cariñoso y tranquilo", new Location(), List.of()),
				new Animal(3, LocalDate.of(2024, 3, 5), "Kira", "Conejo", 1, "Enano", "Pequeño", true, false, 50.0f, "Le encanta saltar", new Location(), List.of())
		);

		List<AnimalOutDTO> mockAnimalOutDTOList = List.of(
				new AnimalOutDTO(1, LocalDate.of(2023, 1, 10), "Luna", "Perro", 3, "Labrador", "Grande", true, false, 150.0f, "Muy amigable", 0, null),
				new AnimalOutDTO(2, LocalDate.now(), "Milo", "Gato", 2, "Siamés", "Pequeño", false, true, 100.0f, "Cariñoso y tranquilo", 0L, null),
				new AnimalOutDTO(3, LocalDate.of(2024, 3, 5), "Kira", "Conejo", 1, "Enano", "Pequeño", true, false, 50.0f, "Le encanta saltar", 0, null)
		);

		when(animalRepository.findAll()).thenReturn(mockAnimalList);
		when(modelMapper.map(mockAnimalList, new TypeToken<List<AnimalOutDTO>>(){}.getType())).thenReturn(mockAnimalOutDTOList);

		List<AnimalOutDTO> result = animalService.getAnimals();

		assertEquals(3, result.size());
		assertEquals("Kira", result.get(2).getName());
		assertEquals("Grande", result.get(0).getSize());
		assertEquals(true, result.get(1).isAdopted());
		assertEquals(LocalDate.now(), result.get(1).getIncorporationDate());

		verify(animalRepository, times(1)).findAll();
		verify(animalRepository, times(0)).findByAge(0);
		verify(animalRepository, times(0)).findUnadoptedAnimalsByLocation(0L);
	}

	@Test
	public void testGetAnimalsBySpecies() {
		String species = "Gato";

		List<Animal> mockAnimalList = List.of(
				new Animal(1, LocalDate.of(2022, 5, 20), "Milo", "Gato", 2, "Siamés", "Pequeño", false, true, 100.0f, "Cariñoso y tranquilo", new Location(), List.of()),
				new Animal(2, LocalDate.of(2021, 11, 15), "Nala", "Gato", 4, "Persa", "Mediano", true, false, 120.0f, "Juguetona y tranquila", new Location(), List.of())
		);

		List<AnimalOutDTO> mockAnimalDTOList = List.of(
				new AnimalOutDTO(1, LocalDate.of(2022, 5, 20), "Milo", "Gato", 2, "Siamés", "Pequeño", false, true, 100.0f, "Cariñoso y tranquilo", 0, null),
				new AnimalOutDTO(2, LocalDate.of(2021, 11, 15), "Nala", "Gato", 4, "Persa", "Mediano", true, false, 120.0f, "Juguetona y tranquila", 0, null)
		);

		when(animalRepository.findBySpecies(species)).thenReturn(mockAnimalList);
		when(modelMapper.map(mockAnimalList, new TypeToken<List<AnimalOutDTO>>() {}.getType())).thenReturn(mockAnimalDTOList);

		List<AnimalOutDTO> result = animalService.findBySpecies(species);

		assertEquals(2, result.size());
		assertEquals(LocalDate.of(2022, 5, 20), result.get(0).getIncorporationDate());
		assertEquals(false, result.get(0).isNeutered());
		assertEquals(4, result.get(1).getAge());
		assertEquals("Juguetona y tranquila", result.get(1).getDescription());

		verify(animalRepository, times(1)).findBySpecies(species);
	}

	@Test
	public void testGetAnimalsByAge() {
		int age = 3;

		List<Animal> mockAnimalList = List.of(
				new Animal(1, LocalDate.of(2023, 3, 10), "Simba", "Perro", 3, "Labrador", "Grande", true, false, 150.0f, "Activo, le encanta correr y jugar con pelotas.", new Location(), List.of()),
				new Animal(2, LocalDate.of(2023, 6, 5), "Luna", "Gato", 3, "Angora", "Pequeño", false, true, 95.0f, "Tranquila, le gusta dormir cerca de la ventana.", new Location(), List.of()),
				new Animal(3, LocalDate.of(2023, 1, 25), "Rocky", "Conejo", 3, "Enano", "Pequeño", true, false, 60.0f, "Muy sociable y curioso, ideal para familias.", new Location(), List.of())

		);

		List<AnimalOutDTO> mockAnimalDTOList = List.of(
				new AnimalOutDTO(1, LocalDate.of(2023, 3, 10), "Simba", "Perro", 3, "Labrador", "Grande", true, false, 150.0f, "Activo, le encanta correr y jugar con pelotas.", 0, null),
				new AnimalOutDTO(2, LocalDate.of(2023, 6, 5), "Luna", "Gato", 3, "Angora", "Pequeño", false, true, 95.0f, "Tranquila, le gusta dormir cerca de la ventana.", 0, null),
				new AnimalOutDTO(3, LocalDate.of(2023, 1, 25), "Rocky", "Conejo", 3, "Enano", "Pequeño", true, false, 60.0f, "Muy sociable y curioso, ideal para familias.", 0, null)

		);

		when(animalRepository.findByAge(age)).thenReturn(mockAnimalList);
		when(modelMapper.map(mockAnimalList, new TypeToken<List<AnimalOutDTO>>() {}.getType())).thenReturn(mockAnimalDTOList);

		List<AnimalOutDTO> result = animalService.findByAge(age);

		assertEquals(3, result.size());
		assertEquals(3, result.get(2).getId());
		assertEquals(150.0f, result.get(0).getPrice());

		verify(animalRepository, times(1)).findByAge(age);
	}

	@Test
	public void testGetAnimalsBySize() {
		String size = "Pequeño";

		List<Animal> mockAnimalList = List.of(
				new Animal(2, LocalDate.of(2023, 6, 5), "Luna", "Gato", 12, "Angora", "Pequeño", false, true, 95.0f, "Tranquila, le gusta dormir cerca de la ventana.", new Location(), List.of()),
				new Animal(8, LocalDate.of(2023, 1, 25), "Rocky", "Conejo", 6, "Enano", "Pequeño", true, false, 60.0f, "Muy sociable y curioso, ideal para familias.", new Location(), List.of())

		);

		List<AnimalOutDTO> mockAnimalDTOList = List.of(
				new AnimalOutDTO(2, LocalDate.of(2023, 6, 5), "Luna", "Gato", 12, "Angora", "Pequeño", false, true, 95.0f, "Tranquila, le gusta dormir cerca de la ventana.", 0, null),
				new AnimalOutDTO(8, LocalDate.of(2023, 1, 25), "Rocky", "Conejo", 6, "Enano", "Pequeño", true, false, 60.0f, "Muy sociable y curioso, ideal para familias.", 0, null)

		);

		when(animalRepository.findBySize(size)).thenReturn(mockAnimalList);
		when(modelMapper.map(mockAnimalList, new TypeToken<List<AnimalOutDTO>>() {}.getType())).thenReturn(mockAnimalDTOList);

		List<AnimalOutDTO> result = animalService.findBySize(size);

		assertEquals(2, result.size());
		assertEquals(8, result.get(1).getId());
		assertEquals("Pequeño", result.get(0).getSize());

		verify(animalRepository, times(1)).findBySize(size);
	}

	@Test
	public void testGetAnimalsBySpeciesAndAge() {
		String species = "Conejo";
		int age = 6;

		List<Animal> mockAnimalList = List.of(
				new Animal(2, LocalDate.of(2023, 6, 5), "Luna", "Conejo", 6, "Angora", "Mediano", false, true, 95.0f, "Tranquila, le gusta dormir cerca de la ventana.", new Location(), List.of()),
				new Animal(8, LocalDate.of(2023, 1, 25), "Rocky", "Conejo", 6, "Enano", "Pequeño", true, false, 60.0f, "Muy sociable y curioso, ideal para familias.", new Location(), List.of())

		);

		List<AnimalOutDTO> mockAnimalDTOList = List.of(
				new AnimalOutDTO(2, LocalDate.of(2023, 6, 5), "Luna", "Conejo", 6, "Angora", "Mediano", false, true, 95.0f, "Tranquila, le gusta dormir cerca de la ventana.", 0, null),
				new AnimalOutDTO(8, LocalDate.of(2023, 1, 25), "Rocky", "Conejo", 6, "Enano", "Pequeño", true, false, 60.0f, "Muy sociable y curioso, ideal para familias.", 0, null)

		);

		when(animalRepository.findBySpeciesAndAge(species, age)).thenReturn(mockAnimalList);
		when(modelMapper.map(mockAnimalList, new TypeToken<List<AnimalOutDTO>>() {}.getType())).thenReturn(mockAnimalDTOList);

		List<AnimalOutDTO> result = animalService.findBySpeciesAndAge(species, age);

		assertEquals(2, result.size());
		assertEquals("Conejo", result.get(1).getSpecies());
		assertEquals(6, result.get(0).getAge());

		verify(animalRepository, times(1)).findBySpeciesAndAge(species, age);
	}

	@Test
	public void testGetAnimalsBySpeciesAndSize() {
		String species = "Conejo";
		String size = "Pequeño";

		List<Animal> mockAnimalList = List.of(
				new Animal(2, LocalDate.of(2023, 6, 5), "Luna", "Conejo", 8, "Angora", "Pequeño", false, true, 95.0f, "Tranquila, le gusta dormir cerca de la ventana.", new Location(), List.of()),
				new Animal(8, LocalDate.of(2023, 1, 25), "Rocky", "Conejo", 6, "Enano", "Pequeño", true, false, 60.0f, "Muy sociable y curioso, ideal para familias.", new Location(), List.of()),
				new Animal(12, LocalDate.of(2022, 11, 10), "Nube", "Conejo", 5, "Cabeza de León", "Pequeño", false, true, 85.0f, "Es tímida al principio, pero muy cariñosa cuando entra en confianza.", new Location(), List.of()),
				new Animal(15, LocalDate.of(2023, 3, 3), "Toby", "Conejo", 4, "Mini Lop", "Pequeño", true, true, 75.0f, "Juguetón y activo, le encanta correr por el jardín.", new Location(), List.of())
		);

		List<AnimalOutDTO> mockAnimalDTOList = List.of(
				new AnimalOutDTO(2, LocalDate.of(2023, 6, 5), "Luna", "Conejo", 8, "Angora", "Pequeño", false, true, 95.0f, "Tranquila, le gusta dormir cerca de la ventana.", 0, null),
				new AnimalOutDTO(8, LocalDate.of(2023, 1, 25), "Rocky", "Conejo", 6, "Enano", "Pequeño", true, false, 60.0f, "Muy sociable y curioso, ideal para familias.", 0, null),
				new AnimalOutDTO(12, LocalDate.of(2022, 11, 10), "Nube", "Conejo", 5, "Cabeza de León", "Pequeño", false, true, 85.0f, "Es tímida al principio, pero muy cariñosa cuando entra en confianza.", 0, null),
				new AnimalOutDTO(15, LocalDate.of(2023, 3, 3), "Toby", "Conejo", 4, "Mini Lop", "Pequeño", true, true, 75.0f, "Juguetón y activo, le encanta correr por el jardín.", 0, null)
		);

		when(animalRepository.findBySpeciesAndSize(species, size)).thenReturn(mockAnimalList);
		when(modelMapper.map(mockAnimalList, new TypeToken<List<AnimalOutDTO>>() {}.getType())).thenReturn(mockAnimalDTOList);

		List<AnimalOutDTO> result = animalService.findBySpeciesAndSize(species, size);

		assertEquals(4, result.size());
		assertEquals("Conejo", result.get(3).getSpecies());
		assertEquals("Pequeño", result.get(1).getSize());

		verify(animalRepository, times(1)).findBySpeciesAndSize(species, size);
	}

	@Test
	public void testGetAnimalsByAgeAndSize() {
		int age = 8;
		String size = "Pequeño";

		List<Animal> mockAnimalList = List.of(
				new Animal(2, LocalDate.of(2023, 6, 5), "Luna", "Conejo", 8, "Angora", "Pequeño", false, true, 95.0f, "Tranquila, le gusta dormir cerca de la ventana.", new Location(), List.of()),
				new Animal(8, LocalDate.of(2023, 1, 25), "Rocky", "Conejo", 8, "Enano", "Pequeño", true, false, 60.0f, "Muy sociable y curioso, ideal para familias.", new Location(), List.of()),
				new Animal(12, LocalDate.of(2022, 11, 10), "Nube", "Conejo", 8, "Cabeza de León", "Pequeño", false, true, 85.0f, "Es tímida al principio, pero muy cariñosa cuando entra en confianza.", new Location(), List.of()),
				new Animal(15, LocalDate.of(2023, 3, 3), "Toby", "Conejo", 8, "Mini Lop", "Pequeño", true, true, 75.0f, "Juguetón y activo, le encanta correr por el jardín.", new Location(), List.of())
		);

		List<AnimalOutDTO> mockAnimalDTOList = List.of(
				new AnimalOutDTO(2, LocalDate.of(2023, 6, 5), "Luna", "Conejo", 8, "Angora", "Pequeño", false, true, 95.0f, "Tranquila, le gusta dormir cerca de la ventana.", 0, null),
				new AnimalOutDTO(8, LocalDate.of(2023, 1, 25), "Rocky", "Conejo", 8, "Enano", "Pequeño", true, false, 60.0f, "Muy sociable y curioso, ideal para familias.", 0, null),
				new AnimalOutDTO(12, LocalDate.of(2022, 11, 10), "Nube", "Conejo", 8, "Cabeza de León", "Pequeño", false, true, 85.0f, "Es tímida al principio, pero muy cariñosa cuando entra en confianza.", 0, null),
				new AnimalOutDTO(15, LocalDate.of(2023, 3, 3), "Toby", "Conejo", 8, "Mini Lop", "Pequeño", true, true, 75.0f, "Juguetón y activo, le encanta correr por el jardín.", 0, null)
		);

		when(animalRepository.findByAgeAndSize(age, size)).thenReturn(mockAnimalList);
		when(modelMapper.map(mockAnimalList, new TypeToken<List<AnimalOutDTO>>() {}.getType())).thenReturn(mockAnimalDTOList);

		List<AnimalOutDTO> result = animalService.findByAgeAndSize(age, size);

		assertEquals(4, result.size());
		assertEquals(8, result.get(3).getAge());
		assertEquals("Pequeño", result.get(1).getSize());

		verify(animalRepository, times(1)).findByAgeAndSize(age, size);
	}

	@Test
	public void testGetAnimalsBySpeciesAndAgeAndSize() {
		String species = "Conejo";
		int age = 8;
		String size = "Pequeño";

		List<Animal> mockAnimalList = List.of(
				new Animal(12, LocalDate.of(2022, 11, 10), "Nube", "Conejo", 8, "Cabeza de León", "Pequeño", false, true, 85.0f, "Es tímida al principio, pero muy cariñosa cuando entra en confianza.", new Location(), List.of())
		);

		List<AnimalOutDTO> mockAnimalDTOList = List.of(
				new AnimalOutDTO(12, LocalDate.of(2022, 11, 10), "Nube", "Conejo", 8, "Cabeza de León", "Pequeño", false, true, 85.0f, "Es tímida al principio, pero muy cariñosa cuando entra en confianza.", 0, null)
		);

		when(animalRepository.findBySpeciesAndAgeAndSize(species, age, size)).thenReturn(mockAnimalList);
		when(modelMapper.map(mockAnimalList, new TypeToken<List<AnimalOutDTO>>() {}.getType())).thenReturn(mockAnimalDTOList);

		List<AnimalOutDTO> result = animalService.findBySpeciesAndAgeAndSize(species, age, size);

		assertEquals(1, result.size());
		assertEquals("Conejo", result.get(0).getSpecies());
		assertEquals(8, result.get(0).getAge());
		assertEquals("Pequeño", result.get(0).getSize());

		verify(animalRepository, times(1)).findBySpeciesAndAgeAndSize(species, age, size);
	}

	@Test
	public void testGetUnadoptedAnimalsByLocation() {
		int locationId = 3;
		Location mockLocation = new Location(3, true, "Calle del Gato 23", "50003", "Zaragoza", "Centro de adopción de animales en Zaragoza.", new ArrayList<>());

		List<Animal> mockAnimalList = List.of(
				new Animal(2, LocalDate.of(2023, 6, 5), "Luna", "Conejo", 8, "Angora", "Pequeño", false, false, 95.0f, "Tranquila, le gusta dormir cerca de la ventana.", mockLocation, List.of()),
				new Animal(8, LocalDate.of(2023, 1, 25), "Rocky", "Conejo", 8, "Enano", "Pequeño", true, false, 60.0f, "Muy sociable y curioso, ideal para familias.", mockLocation, List.of()),
				new Animal(20, LocalDate.of(2022, 9, 15), "Max", "Perro", 5, "Labrador", "Grande", true, false, 120.0f, "Muy activo y cariñoso. Le gusta jugar con pelotas.", mockLocation, List.of()),
				new Animal(21, LocalDate.of(2024, 2, 10), "Misu", "Gato", 2, "Siamés", "Mediano", false, false, 80.0f, "Curiosa y juguetona. Ideal para apartamento.", mockLocation, List.of()),
				new Animal(22, LocalDate.of(2023, 11, 20), "Firu", "Hurón", 1, "Albino", "Pequeño", true, false, 50.0f, "Muy inquieto, necesita espacio para explorar.", mockLocation, List.of())
		);

		List<AnimalOutDTO> mockAnimalDTOList = List.of(
				new AnimalOutDTO(2, LocalDate.of(2023, 6, 5), "Luna", "Conejo", 8, "Angora", "Pequeño", false, false, 95.0f, "Tranquila, le gusta dormir cerca de la ventana.", locationId, "Zaragoza"),
				new AnimalOutDTO(8, LocalDate.of(2023, 1, 25), "Rocky", "Conejo", 8, "Enano", "Pequeño", true, false, 60.0f, "Muy sociable y curioso, ideal para familias.", locationId, "Zaragoza"),
				new AnimalOutDTO(20, LocalDate.of(2022, 9, 15), "Max", "Perro", 5, "Labrador", "Grande", true, false, 120.0f, "Muy activo y cariñoso. Le gusta jugar con pelotas.", locationId, "Zaragoza"),
				new AnimalOutDTO(21, LocalDate.of(2024, 2, 10), "Misu", "Gato", 2, "Siamés", "Mediano", false, false, 80.0f, "Curiosa y juguetona. Ideal para apartamento.", locationId, "Zaragoza"),
				new AnimalOutDTO(22, LocalDate.of(2023, 11, 20), "Firu", "Hurón", 1, "Albino", "Pequeño", true, false, 50.0f, "Muy inquieto, necesita espacio para explorar.", locationId, "Zaragoza")
		);

		when(animalRepository.findUnadoptedAnimalsByLocation(locationId)).thenReturn(mockAnimalList);
		when(modelMapper.map(mockAnimalList, new TypeToken<List<AnimalOutDTO>>() {}.getType())).thenReturn(mockAnimalDTOList);

		List<AnimalOutDTO> result = animalService.findUnadoptedAnimalsByLocation(locationId);

		assertEquals(5, result.size());
		assertEquals(false, result.get(0).isAdopted());
		assertEquals(false, result.get(2).isAdopted());
		assertEquals("Hurón", result.get(4).getSpecies());

		verify(animalRepository, times(1)).findUnadoptedAnimalsByLocation(locationId);
	}

	//endregion

	//region POST

	@Test
	public void testAddAnimal() {
		long locationId = 1;
		Location mockLocation = new Location(1, true, "Calle del Gato 23", "50003", "Zaragoza", "Centro de adopción de animales en Zaragoza.", new ArrayList<>());
		AnimalInDTO mockAnimalInDTO = new AnimalInDTO("Bigotes","Gato",2,"Persa","Pequeño",true,false,120.0f,"Es alegre y le gusta dormir en el sofá");
		AnimalOutDTO mockAnimalOutDTO = new AnimalOutDTO(1, LocalDate.of(2021, 11, 15), "Bigotes", "Gato", 4, "Persa", "Pequeño", true, false, 120.0f, "Es alegre y le gusta dormir en el sofá", 0, null);
		Animal mockAnimal = new Animal(1, LocalDate.of(2021, 11, 15), "Bigotes", "Gato", 4, "Persa", "Pequeño", true, false, 120.0f, "Es alegre y le gusta dormir en el sofá", new Location(), List.of());

		when(locationRepository.findById(locationId)).thenReturn(Optional.of(mockLocation));
		when(modelMapper.map(mockAnimalInDTO, Animal.class)).thenReturn(mockAnimal);
		when(modelMapper.map(mockAnimal, AnimalOutDTO.class)).thenReturn(mockAnimalOutDTO);

		AnimalOutDTO result = null;
		try{
			result = animalService.saveAnimal(locationId, mockAnimalInDTO);
		} catch (ResourceNotFoundException ex){}

		assertEquals(1, result.getId());
		assertEquals("Persa", result.getBreed());
		assertEquals(120.0f, result.getPrice());

		verify(animalRepository, times(1)).save(mockAnimal);
	}

	@Test
	public void testAddAnimalWhenLocationIsNotFound() {
		long locationId = 32;
		AnimalInDTO mockAnimalInDTO = new AnimalInDTO("Bigotes","Gato",2,"Persa","Pequeño",true,false,120.0f,"Es alegre y le gusta dormir en el sofá");

		when(locationRepository.findById(locationId)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> animalService.saveAnimal(locationId, mockAnimalInDTO));
	}

	//endregion

	// region DELETE

	@Test
	public void testRemoveAnimal(){
		long animalId = 1;

		when(animalRepository.existsById(animalId)).thenReturn(true);

		animalService.removeAnimal(animalId);

		verify(animalRepository, times(1)).deleteById(animalId);
	}

	// endregion

	//region PUT

	@Test
	public void testModifyAnimal() {
		long animalId = 1;
		Animal mockExistingAnimal = new Animal(1, LocalDate.of(2021, 11, 15), "Bigotes", "Gato", 4, "Persa", "Pequeño", true, false, 120.0f, "Es alegre y le gusta dormir en el sofá", new Location(), List.of());
		AnimalInDTO mockAnimalInDTO = new AnimalInDTO("Bigotes","Gato",4,"Persa","Pequeño",true,true,128.7f,"Es alegre y le gusta dormir en el sofá. Se lleva muy bien con perros y niños.");
		Animal mockMappedAnimal = new Animal(1, LocalDate.of(2021, 11, 15), "Bigotes", "Gato", 4, "Persa", "Pequeño", true, false, 128.7f, "Es alegre y le gusta dormir en el sofá. Se lleva muy bien con perros y niños.", new Location(), List.of());
		AnimalOutDTO mockAnimalOutDTO = new AnimalOutDTO(1, LocalDate.of(2021, 11, 15), "Bigotes", "Gato", 4, "Persa", "Pequeño", true, true, 128.7f, "Es alegre y le gusta dormir en el sofá. Se lleva muy bien con perros y niños.", 0, null);

		when(animalRepository.findById(animalId)).thenReturn(Optional.of(mockExistingAnimal));
		when(modelMapper.map(mockAnimalInDTO, Animal.class)).thenReturn(mockMappedAnimal);
		when(modelMapper.map(mockExistingAnimal, AnimalOutDTO.class)).thenReturn(mockAnimalOutDTO);

		AnimalOutDTO result = animalService.modifyAnimal(mockAnimalInDTO, animalId);

		assertEquals("Bigotes", result.getName());
		assertEquals("Gato", result.getSpecies());
		assertEquals(4, result.getAge());
		assertEquals(128.7f, result.getPrice());
		assertEquals("Es alegre y le gusta dormir en el sofá. Se lleva muy bien con perros y niños.", result.getDescription());

		verify(animalRepository, times(1)).save(mockExistingAnimal);
	}

	@Test
	public void testModifyAnimalLocation() {
		long animalId = 1;
		long locationId = 1;
		Animal mockExistingAnimal = new Animal(1, LocalDate.of(2021, 11, 15), "Bigotes", "Gato", 4, "Persa", "Pequeño", true, false, 120.0f, "Es alegre y le gusta dormir en el sofá", new Location(), List.of());
		Location mockLocation = new Location(1, true, "Calle del Gato 23", "50003", "Zaragoza", "Centro de adopción de animales en Zaragoza.", new ArrayList<>());
		AnimalOutDTO mockAnimalOutDTO = new AnimalOutDTO(1, LocalDate.of(2021, 11, 15), "Bigotes", "Gato", 4, "Persa", "Pequeño", true, false, 120.0f, "Es alegre y le gusta dormir en el sofá.", 1, "Zaragoza");

		when(animalRepository.findById(animalId)).thenReturn(Optional.of(mockExistingAnimal));
		when(locationRepository.findById(locationId)).thenReturn(Optional.of(mockLocation));
		when(modelMapper.map(mockExistingAnimal, AnimalOutDTO.class)).thenReturn(mockAnimalOutDTO);

		AnimalOutDTO result = animalService.modifyAnimalLocation(animalId, locationId);

		assertEquals(1, result.getLocationId());
		assertEquals("Zaragoza", result.getLocationCity());
		assertEquals("Bigotes", result.getName());

		verify(animalRepository, times(1)).save(mockExistingAnimal);
	}

	//endregion

	//region PATCH

	@Test
	public void testReturnAnimal() {
		long animalId = 1;
		Animal mockExistingAnimal = new Animal(1, LocalDate.of(2021, 11, 15), "Bigotes", "Gato", 4, "Persa", "Pequeño", true, true, 120.0f, "Es alegre y le gusta dormir en el sofá", new Location(), List.of());
		AnimalOutDTO mockAnimalOutDTO = new AnimalOutDTO(1, LocalDate.of(2021, 11, 15), "Bigotes", "Gato", 4, "Persa", "Pequeño", true, false, 128.7f, "Es alegre y le gusta dormir en el sofá.", 0, null);

		when(animalRepository.findById(animalId)).thenReturn(Optional.of(mockExistingAnimal));
		when(modelMapper.map(mockExistingAnimal, AnimalOutDTO.class)).thenReturn(mockAnimalOutDTO);

		AnimalOutDTO result = animalService.returnAnimal(animalId);

		assertEquals(1, result.getId());
		assertEquals(false, result.isAdopted());
		assertEquals("Bigotes", result.getName());

		verify(animalRepository, times(1)).save(mockExistingAnimal);
	}

	//endregion
}
