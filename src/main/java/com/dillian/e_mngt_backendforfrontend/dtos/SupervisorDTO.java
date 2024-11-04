package com.dillian.e_mngt_backendforfrontend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupervisorDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String imageUrl;
    private String biography;
}
