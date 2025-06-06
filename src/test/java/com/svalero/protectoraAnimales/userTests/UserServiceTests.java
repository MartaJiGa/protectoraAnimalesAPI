package com.svalero.protectoraAnimales.userTests;

import com.svalero.protectoraAnimales.domain.*;
import com.svalero.protectoraAnimales.domain.dto.user.UserChangeEmailInDTO;
import com.svalero.protectoraAnimales.domain.dto.user.UserInDTO;
import com.svalero.protectoraAnimales.exception.runtime.ResourceNotFoundException;
import com.svalero.protectoraAnimales.repository.UserRepository;
import com.svalero.protectoraAnimales.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    //region GET

    @Test
    public void testFindUserById() {
        long userId = 2;
        User mockUser = new User(userId, "Lu327", "Lucía", "Gómez", LocalDate.of(1985, 2, 12), "lu327@gmail.com", List.of(), List.of());

        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(mockUser));

        User result = userService.findById(userId);

        assertEquals("Lucía", result.getName());
        assertEquals("lu327@gmail.com", result.getEmail());
        assertEquals(2, result.getId());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testFindUserWhenUserIsNotFound() {
        long userId = 35;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.findById(userId));

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testGetUsers() {
        List<User> mockUserList = List.of(
                new User(1, "Susana24", "Susana", "Martínez", LocalDate.of(1972, 12, 17), "susi@gmail.com", List.of(new Adoption(), new Adoption()), List.of()),
                new User(2, "Solyluna", "Paloma", "Blesa", LocalDate.of(2002, 7, 01), "solyluna@outlook.es", List.of(), List.of())
        );

        when(userRepository.findAll()).thenReturn(mockUserList);

        List<User> result = userService.getUsers();

        assertEquals(2, result.size());
        assertEquals("solyluna@outlook.es", result.get(1).getEmail());
        assertEquals("Susana24", result.get(0).getUsername());
        assertEquals(LocalDate.of(2002, 7, 01), result.get(1).getDateOfBirth());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testGetUsersByName() {
        String name = "Paloma";

        List<User> mockUserList = List.of(
                new User(2, "Solyluna", "Paloma", "Blesa", LocalDate.of(2002, 7, 01), "solyluna@outlook.es", List.of(new Adoption(), new Adoption(), new Adoption()), List.of()),
                new User(3, "Palomita33", "Paloma", "Palacio", LocalDate.of(1995, 4, 9), "palomita33@gmail.com", List.of(), List.of())
        );

        when(userRepository.findByName(name)).thenReturn(mockUserList);

        List<User> result = userService.findByName(name);

        assertEquals(2, result.size());
        assertEquals(3, result.get(0).getAdoptions().size());
        assertEquals("Palacio", result.get(1).getSurname());
        assertEquals("Paloma", result.get(0).getName());

        verify(userRepository, times(1)).findByName(name);
    }

    @Test
    public void testGetUsersBySurname() {
        String surname = "Martínez";

        List<User> mockUserList = List.of(
                new User(1, "Susana24", "Susana", "Martínez", LocalDate.of(1972, 12, 17), "susi@gmail.com", List.of(new Adoption(), new Adoption()), List.of()),
                new User(2, "JM_Martinez", "Javier", "Martínez", LocalDate.of(1985, 5, 21), "javiM@gmail.com", List.of(new Adoption()), List.of()),
                new User(3, "MartinezClau", "Claudia", "Martínez", LocalDate.of(1998, 9, 12), "claudia.martinez@hotmail.com", List.of(), List.of())
        );

        when(userRepository.findBySurname(surname)).thenReturn(mockUserList);

        List<User> result = userService.findBySurname(surname);

        assertEquals(3, result.size());
        assertEquals(LocalDate.of(1998, 9, 12), result.get(2).getDateOfBirth());
        assertEquals("Martínez", result.get(0).getSurname());

        verify(userRepository, times(1)).findBySurname(surname);
    }

    @Test
    public void testGetUsersByNameAndSurname() {
        String name = "Javier";
        String surname = "Martínez";

        List<User> mockUserList = List.of(
                new User(1, "Javiman", "Javier", "Martínez", LocalDate.of(1989, 03, 13), "javiman@yahoo.es", List.of(), List.of()),
                new User(2, "JM_Martinez", "Javier", "Martínez", LocalDate.of(1985, 5, 21), "javiM@gmail.com", List.of(new Adoption()), List.of())
        );

        when(userRepository.findByNameAndSurname(name, surname)).thenReturn(mockUserList);

        List<User> result = userService.findByNameAndSurname(name, surname);

        assertEquals(2, result.size());
        assertEquals("Javier", result.get(1).getName());
        assertEquals("Martínez", result.get(0).getSurname());

        verify(userRepository, times(1)).findByNameAndSurname(name, surname);
    }

    @Test
    public void testGetUsersWithAdoptionsAndDonations() {
        List<User> mockUserList = List.of(
                new User(2, "Solyluna", "Paloma", "Blesa", LocalDate.of(2002, 7, 01), "solyluna@outlook.es", List.of(new Adoption(), new Adoption()), List.of(new Donation()))
        );

        when(userRepository.findUsersWithAdoptionsAndDonations()).thenReturn(mockUserList);

        List<User> result = userService.findUsersWithAdoptionsAndDonations();

        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getAdoptions().size());
        assertEquals(1, result.get(0).getDonations().size());

        verify(userRepository, times(1)).findUsersWithAdoptionsAndDonations();
    }

    //endregion

    //region POST

    @Test
    public void testAddUser() {
        UserInDTO mockUserInDTO = new UserInDTO("Lu327", "Lucía", "Gómez", LocalDate.of(1985, 2, 12), "lu327@gmail.com");
        User mockUser = new User(2, "Lu327", "Lucía", "Gómez", LocalDate.of(1985, 2, 12), "lu327@gmail.com", List.of(), List.of());

        when(modelMapper.map(mockUserInDTO, User.class)).thenReturn(mockUser);
        when(userRepository.save(mockUser)).thenReturn(mockUser);

        User result = userService.saveUser(mockUserInDTO);

        assertNotNull(result);
        assertEquals(2, result.getId());
        assertEquals("Gómez", result.getSurname());
        assertEquals(LocalDate.of(1985, 2, 12), result.getDateOfBirth());

        verify(userRepository, times(1)).save(mockUser);
    }

    //endregion

    //region DELETE

    @Test
    public void testRemoveUser(){
        long userId = 2;

        when(userRepository.existsById(userId)).thenReturn(true);

        userService.removeUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    public void testRemoveUserWhenUserIsNotFound(){
        long userId = 1;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> userService.removeUser(userId));

        verify(userRepository, never()).deleteById(userId);
    }

    //endregion

    //region PUT

    @Test
    public void testModifyUser() {
        long userId = 2;
        UserInDTO mockUserInDTO = new UserInDTO("LuGom", "Lucía", "Gómez", LocalDate.of(1985, 2, 12), "luGom@gmail.com");
        User mockExistingUser = new User(2, "Lu327", "Lucía", "Gómez", LocalDate.of(1985, 2, 12), "lu327@gmail.com", List.of(), List.of());
        User mockUser = new User(2, "LuGom", "Lucía", "Gómez", LocalDate.of(1985, 2, 12), "luGom@gmail.com", List.of(), List.of());

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockExistingUser));
        when(modelMapper.map(mockUserInDTO, User.class)).thenReturn(mockUser);

        mockExistingUser.setUsername(mockUser.getUsername());
        mockExistingUser.setName(mockUser.getName());
        mockExistingUser.setSurname(mockUser.getSurname());
        mockExistingUser.setDateOfBirth(mockUser.getDateOfBirth());
        mockExistingUser.setEmail(mockUser.getEmail());

        when(userRepository.save(mockExistingUser)).thenReturn(mockExistingUser);

        User result = userService.modifyUser(mockUserInDTO, userId);

        assertEquals("LuGom", result.getUsername());
        assertEquals("luGom@gmail.com", result.getEmail());

        verify(userRepository, times(1)).save(mockExistingUser);
    }

    @Test
    public void testModifyUserWhenUserIsNotFound() {
        long userId = 1;
        UserInDTO mockUserInDTO = new UserInDTO("LuGom", "Lucía", "Gómez", LocalDate.of(1985, 2, 12), "luGom@gmail.com");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.modifyUser(mockUserInDTO, userId));

        verify(userRepository, never()).save(any());
    }

    //endregion

    //region PATCH

    @Test
    public void testChangeUserEmailSuccess() {
        long userId = 2;
        UserChangeEmailInDTO userChangeEmailDTO = new UserChangeEmailInDTO("luGom@gmail.com");
        User mockExistingUser = new User(2, "Lu327", "Lucía", "Gómez", LocalDate.of(1985, 2, 12), "lu327@gmail.com", List.of(), List.of());

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockExistingUser));

        mockExistingUser.setEmail(userChangeEmailDTO.getEmail());

        when(userRepository.save(mockExistingUser)).thenReturn(mockExistingUser);

        User result = userService.changeUserEmail(userId, userChangeEmailDTO);

        assertEquals("luGom@gmail.com", result.getEmail());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(mockExistingUser);
    }

    @Test
    public void testChangeUserEmailUserNotFound() {
        long userId = 1;
        UserChangeEmailInDTO userChangeEmailDTO = new UserChangeEmailInDTO("luGom@gmail.com");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.changeUserEmail(userId, userChangeEmailDTO));
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any());
    }

    //endregion
}
