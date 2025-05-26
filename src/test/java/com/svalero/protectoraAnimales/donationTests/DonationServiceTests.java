package com.svalero.protectoraAnimales.donationTests;

import com.svalero.protectoraAnimales.repository.DonationRepository;
import com.svalero.protectoraAnimales.service.DonationService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
public class DonationServiceTests {

    @InjectMocks
    private DonationService donationService;

    @Mock
    private DonationRepository donationRepository;

    @Mock
    private ModelMapper modelMapper;

    //region GET
    //endregion

    //region POST
    //endregion

    //region DELETE
    //endregion

    //region PUT
    //endregion

    //region PATCH
    //endregion
}
