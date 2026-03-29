package com.viajes.viajes.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private String nombre;
    private String email;
    private String password;
    private String descripcion;
    private String foto;
}
