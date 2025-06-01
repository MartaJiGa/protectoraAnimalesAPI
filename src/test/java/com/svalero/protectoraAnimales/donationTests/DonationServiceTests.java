package com.svalero.protectoraAnimales.donationTests;

import com.svalero.protectoraAnimales.domain.Donation;
import com.svalero.protectoraAnimales.domain.User;
import com.svalero.protectoraAnimales.domain.dto.donation.DonationInDTO;
import com.svalero.protectoraAnimales.domain.dto.donation.DonationOutDTO;
import com.svalero.protectoraAnimales.domain.dto.donation.DonationSplitPaymentInDTO;
import com.svalero.protectoraAnimales.exception.runtime.ResourceNotFoundException;
import com.svalero.protectoraAnimales.repository.DonationRepository;
import com.svalero.protectoraAnimales.repository.UserRepository;
import com.svalero.protectoraAnimales.service.DonationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class DonationServiceTests {

    @InjectMocks
    private DonationService donationService;

    @Mock
    private DonationRepository donationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    //region GET

    @Test
    public void testGetDonationById() {
        long donationId = 1;
        User user = new User(1, "juan32", "Juan", "Pérez", LocalDate.of(1990, 1, 1), "juan@gmail.com", List.of(), List.of());
        Donation donation = new Donation(donationId, LocalDate.of(2024, 10, 5), 50, "Tarjeta", false, 0, user);

        when(donationRepository.findById(donationId)).thenReturn(Optional.of(donation));

        Donation result = donationService.findById(donationId);

        assertNotNull(result);
        assertEquals(50, result.getQuantity());
        assertEquals("Tarjeta", result.getPaymentType());

        verify(donationRepository, times(1)).findById(donationId);
    }

    @Test
    public void testGetDonationWhenDonationIdNotFound() {
        long donationId = 8;

        when(donationRepository.findById(donationId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> donationService.findById(donationId));

        verify(donationRepository, times(1)).findById(donationId);
    }

    @Test
    public void testGetDonations() {
        List<Donation> donationList = getDonations();
        List<DonationOutDTO> donationOutDTOList = List.of(
                new DonationOutDTO(1, LocalDate.of(2022, 3, 12), 50, "Tarjeta", false, 0, 1, "juan32", "juan@gmail.com"),
                new DonationOutDTO(2, LocalDate.of(2023, 12, 27), 100, "Efectivo", true, 3, 2, "mary", "mary@outlook.com"),
                new DonationOutDTO(3, LocalDate.of(2024, 2, 1), 75, "Transferencia", false, 0, 3, "carloooos", "carloooos@proton.com"),
                new DonationOutDTO(4, LocalDate.of(2024, 7, 3), 30, "Tarjeta", true, 2, 4, "laMorena", "lamorena@gmail.es")
        );

        when(donationRepository.findAll()).thenReturn(donationList);
        when(modelMapper.map(donationList, new TypeToken<List<DonationOutDTO>>() {}.getType())).thenReturn(donationOutDTOList);

        List<DonationOutDTO> result = donationService.getDonations();

        assertEquals(4, result.size());
        assertEquals(3, result.get(2).getUserId());
        assertEquals("juan32", result.get(0).getUserUsername());
        assertEquals("Efectivo", result.get(1).getPaymentType());
        assertTrue(result.get(3).isSplitPayment());

        verify(donationRepository, times(1)).findAll();
    }

    @Test
    public void testGetDonationsByDonationDate() {
        LocalDate donationDate = LocalDate.of(2024, 10, 5);
        List<Donation> donationList = getDonationsByDonationDate(donationDate);
        List<DonationOutDTO> donationOutDTOList = List.of(
                new DonationOutDTO(1, donationDate, 50, "Tarjeta", false, 0, 1, "juan32", "juan@gmail.com"),
                new DonationOutDTO(2, donationDate, 100, "Efectivo", true, 3, 2, "mary", "mary@outlook.com")
        );

        when(donationRepository.findByDonationDate(donationDate)).thenReturn(donationList);
        when(modelMapper.map(donationList, new TypeToken<List<DonationOutDTO>>() {}.getType())).thenReturn(donationOutDTOList);

        List<DonationOutDTO> result = donationService.findByDonationDate(donationDate);

        assertEquals(2, result.size());
        assertEquals("juan@gmail.com", result.get(0).getUserEmail());
        assertEquals("Efectivo", result.get(1).getPaymentType());
        assertEquals(100, result.get(1).getQuantity());

        verify(donationRepository, times(1)).findByDonationDate(donationDate);
    }

    @Test
    public void testGetDonationsByUserId() {
        User user = new User(2, "mary", "María", "López", LocalDate.of(1992, 3, 15), "mary@outlook.com", List.of(), List.of());

        List<Donation> donationList = List.of(
                new Donation(1, LocalDate.of(2022, 3, 12), 50, "Tarjeta", false, 0, user),
                new Donation(3, LocalDate.of(2024, 2, 1), 75, "Transferencia", false, 0, user)
        );

        List<DonationOutDTO> donationOutDTOList = List.of(
                new DonationOutDTO(1, LocalDate.of(2022, 3, 12), 50, "Tarjeta", false, 0, 2, "mary", "mary@outlook.com"),
                new DonationOutDTO(3, LocalDate.of(2024, 2, 1), 75, "Transferencia", false, 0, 2, "mary", "mary@outlook.com")
        );

        when(donationRepository.findByUserId(user.getId())).thenReturn(donationList);
        when(modelMapper.map(donationList, new TypeToken<List<DonationOutDTO>>() {}.getType())).thenReturn(donationOutDTOList);

        List<DonationOutDTO> result = donationService.findByUserId(user.getId());

        assertEquals(2, result.size());
        assertEquals("mary", result.get(0).getUserUsername());
        assertEquals(0, result.get(1).getSplitPaymentQuantity());

        verify(donationRepository, times(1)).findByUserId(user.getId());
    }

    @Test
    public void testGetDonationsByDonationDateAndUserId() {
        LocalDate donationDate = LocalDate.of(2024, 10, 5);
        User user = new User(2, "mary", "María", "López", LocalDate.of(1992, 3, 15), "mary@outlook.com", List.of(), List.of());

        List<Donation> donationList = List.of(
                new Donation(1, donationDate, 50, "Tarjeta", false, 0, user)
        );

        List<DonationOutDTO> donationOutDTOList = List.of(
                new DonationOutDTO(1, donationDate, 50, "Tarjeta", false, 0, user.getId(), user.getUsername(), user.getEmail())
        );

        when(donationRepository.findByDonationDateAndUserId(donationDate, user.getId())).thenReturn(donationList);
        when(modelMapper.map(donationList, new TypeToken<List<DonationOutDTO>>() {}.getType())).thenReturn(donationOutDTOList);

        List<DonationOutDTO> result = donationService.findByDonationDateAndUserId(donationDate, user.getId());

        assertEquals(1, result.size());
        assertEquals("Tarjeta", result.get(0).getPaymentType());
        assertEquals("mary@outlook.com", result.get(0).getUserEmail());

        verify(donationRepository, times(1)).findByDonationDateAndUserId(donationDate, user.getId());
    }

    //endregion

    //region POST

    @Test
    public void testAddDonation() {
        User user = new User(1, "juan32", "Juan", "Pérez", LocalDate.of(1990, 1, 1), "juan@gmail.com", List.of(), List.of());
        DonationInDTO donationInDTO = new DonationInDTO(50, "Tarjeta", false, 0);
        Donation donation = new Donation(1, LocalDate.now(), 50, "Tarjeta", false, 0, user);
        DonationOutDTO donationOutDTO = new DonationOutDTO(1, donation.getDonationDate(), 50, "Tarjeta", false, 0, 1, "juan32", "juan@gmail.com");

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(modelMapper.map(donationInDTO, Donation.class)).thenReturn(donation);
        when(modelMapper.map(donation, DonationOutDTO.class)).thenReturn(donationOutDTO);

        DonationOutDTO result = donationService.saveDonation(user.getId(), donationInDTO);

        assertEquals(1, result.getId());
        assertEquals("juan32", result.getUserUsername());
        assertEquals("Tarjeta", result.getPaymentType());
        assertEquals(50, result.getQuantity());

        verify(userRepository, times(1)).findById(user.getId());
        verify(donationRepository, times(1)).save(donation);
    }

    @Test
    public void testAddDonationWhenUserIsNotFound() {
        long userId = 8;
        DonationInDTO donationInDTO = new DonationInDTO(100, "Efectivo", true, 2);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> donationService.saveDonation(userId, donationInDTO));

        verify(userRepository, times(1)).findById(userId);
        verify(donationRepository, never()).save(any(Donation.class));
    }

    //endregion

    //region DELETE

    @Test
    public void testRemoveDonation() {
        long donationId = 2;

        when(donationRepository.existsById(donationId)).thenReturn(true);

        donationService.removeDonation(donationId);

        verify(donationRepository, times(1)).deleteById(donationId);
    }

    @Test
    public void testRemoveDonationWhenDonationIsNotFound() {
        long donationId = 1;

        when(donationRepository.existsById(donationId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> donationService.removeDonation(donationId));

        verify(donationRepository, never()).deleteById(donationId);
    }

    //endregion

    //region PUT

    @Test
    public void testModifyDonation() {
        User existingUser = new User(2, "mary", "María", "López", LocalDate.of(1992, 3, 15), "mary@outlook.com", List.of(), List.of());
        User newUser = new User(4, "laMorena", "Laura", "Moreno", LocalDate.of(1995, 11, 5), "lamorena@gmail.es", List.of(), List.of());

        Donation existingDonation = new Donation(1, LocalDate.of(2024, 1, 1), 50, "Tarjeta", false, 0, existingUser);
        DonationInDTO donationInDTO = new DonationInDTO(75, "Efectivo", true, 3);
        Donation donation = new Donation(0, null, 75, "Efectivo", true, 3, null);
        DonationOutDTO donationOutDTO = new DonationOutDTO(existingDonation.getId(), existingDonation.getDonationDate(), 75, "Efectivo", true, 3, newUser.getId(), "mary", "mary@outlook.com");

        when(donationRepository.findById(existingDonation.getId())).thenReturn(Optional.of(existingDonation));
        when(userRepository.findById(newUser.getId())).thenReturn(Optional.of(newUser));
        when(modelMapper.map(donationInDTO, Donation.class)).thenReturn(donation);
        when(modelMapper.map(existingDonation, DonationOutDTO.class)).thenReturn(donationOutDTO);
        when(donationRepository.save(existingDonation)).thenReturn(existingDonation);

        DonationOutDTO result = donationService.modifyDonation(existingDonation.getId(), newUser.getId(), donationInDTO);

        assertEquals(75, result.getQuantity());
        assertEquals("Efectivo", result.getPaymentType());
        assertEquals(true, result.isSplitPayment());
        assertEquals(3, result.getSplitPaymentQuantity());
        assertEquals(4, result.getUserId());

        verify(donationRepository, times(1)).save(existingDonation);
    }

    @Test
    public void testModifyDonationWhenDonationNotFound() {
        long donationId = 8;
        long userId = 4;
        DonationInDTO donationInDTO = new DonationInDTO(75, "Efectivo", true, 3);

        when(donationRepository.findById(donationId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> donationService.modifyDonation(donationId, userId, donationInDTO));

        verify(donationRepository, times(1)).findById(donationId);
        verify(donationRepository, never()).save(any());
    }

    @Test
    public void testModifyDonationWhenUserNotFound() {
        long donationId = 1;
        long userId = 6;

        Donation existingDonation = new Donation(donationId, LocalDate.of(2024, 1, 1), 50, "Tarjeta", false, 0, null);
        DonationInDTO donationInDTO = new DonationInDTO(75, "Efectivo", true, 3);

        when(donationRepository.findById(donationId)).thenReturn(Optional.of(existingDonation));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> donationService.modifyDonation(donationId, userId, donationInDTO));

        verify(donationRepository, times(1)).findById(donationId);
        verify(userRepository, times(1)).findById(userId);
        verify(donationRepository, never()).save(any());
    }

    //endregion

    //region PATCH

    @Test
    public void testSplitPaymentSuccess() {
        long donationId = 1;
        DonationSplitPaymentInDTO splitPaymentDTO = new DonationSplitPaymentInDTO(true, 4);

        User user = new User(1, "juan32", "Juan", "Pérez", LocalDate.of(1990, 1, 1), "juan@gmail.com", List.of(), List.of());
        Donation existingDonation = new Donation(donationId, LocalDate.of(2024, 5, 1), 100, "Tarjeta", false, 0, user);
        DonationOutDTO donationOutDTO = new DonationOutDTO(donationId, existingDonation.getDonationDate(), existingDonation.getQuantity(), existingDonation.getPaymentType(), splitPaymentDTO.isSplitPayment(), splitPaymentDTO.getSplitPaymentQuantity(), user.getId(), user.getUsername(), user.getEmail());

        when(donationRepository.findById(donationId)).thenReturn(Optional.of(existingDonation));

        existingDonation.setSplitPayment(splitPaymentDTO.isSplitPayment());
        existingDonation.setSplitPaymentQuantity(splitPaymentDTO.getSplitPaymentQuantity());

        when(donationRepository.save(existingDonation)).thenReturn(existingDonation);
        when(modelMapper.map(existingDonation, DonationOutDTO.class)).thenReturn(donationOutDTO);

        DonationOutDTO result = donationService.splitPayment(donationId, splitPaymentDTO);

        assertTrue(result.isSplitPayment());
        assertEquals(4, result.getSplitPaymentQuantity());
        assertEquals(donationId, result.getId());

        verify(donationRepository, times(1)).findById(donationId);
        verify(donationRepository, times(1)).save(existingDonation);
    }

    @Test
    public void testSplitPaymentDonationNotFound() {
        long donationId = 1;
        DonationSplitPaymentInDTO splitPaymentInDTO = new DonationSplitPaymentInDTO(true, 4);

        when(donationRepository.findById(donationId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> donationService.splitPayment(donationId, splitPaymentInDTO));

        verify(donationRepository, times(1)).findById(donationId);
        verify(donationRepository, never()).save(any());
    }

    //endregion

    //region METHODS

    private static List<Donation> getDonations() {
        List<User> userList = List.of(
                new User(1, "juan32", "Juan", "Pérez", LocalDate.of(1990, 1, 1), "juan@gmail.com", List.of(), List.of()),
                new User(2, "mary", "María", "López", LocalDate.of(1992, 3, 15), "mary@outlook.com", List.of(), List.of()),
                new User(3, "carloooos", "Carlos", "Arantegui", LocalDate.of(1988, 7, 20), "carloooos@proton.com", List.of(), List.of()),
                new User(4, "laMorena", "Laura", "Moreno", LocalDate.of(1995, 11, 5), "lamorena@gmail.es", List.of(), List.of())
        );

        return List.of(
                new Donation(1, LocalDate.of(2022, 3, 12), 50, "Tarjeta", false, 0, userList.get(0)),
                new Donation(2, LocalDate.of(2023, 12, 27), 100, "Efectivo", true, 3, userList.get(1)),
                new Donation(3, LocalDate.of(2024, 2, 1), 75, "Transferencia", false, 0, userList.get(2)),
                new Donation(4, LocalDate.of(2024, 7, 3), 30, "Tarjeta", true, 2, userList.get(3))
        );
    }

    private static List<Donation> getDonationsByDonationDate(LocalDate donationDate) {
        List<User> userList = List.of(
                new User(1, "juan32", "Juan", "Pérez", donationDate, "juan@gmail.com", List.of(), List.of()),
                new User(2, "mary", "María", "López", donationDate, "mary@outlook.com", List.of(), List.of())
        );

        return List.of(
                new Donation(1, LocalDate.of(2022, 3, 12), 50, "Tarjeta", false, 0, userList.get(0)),
                new Donation(2, LocalDate.of(2023, 12, 27), 100, "Efectivo", true, 3, userList.get(1))
        );
    }

    //endregion
}
