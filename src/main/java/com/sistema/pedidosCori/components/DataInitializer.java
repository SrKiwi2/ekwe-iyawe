package com.sistema.pedidosCori.components;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.sistema.pedidosCori.models.dao.IPlatoDao;
import com.sistema.pedidosCori.models.entity.Plato;

import lombok.RequiredArgsConstructor;

// @Component
// @RequiredArgsConstructor
// public class DataInitializer implements CommandLineRunner {
//     private final IPlatoDao platoDao;

//     @Override
//     public void run(String... args) {
//         if (platoDao.count() == 0) {
//             platoDao.save(Plato.builder().nombre("Sopa del día").descripcion("Cambia cada día").precio(15.0).categoria("Entradas").disponible(true).build());
//             platoDao.save(Plato.builder().nombre("Ensalada César").descripcion("Lechuga, crutones, parmesano").precio(25.0).categoria("Entradas").disponible(true).build());
//             platoDao.save(Plato.builder().nombre("Pollo a la plancha").descripcion("Con guarnición").precio(45.0).categoria("Platos fuertes").disponible(true).build());
//             platoDao.save(Plato.builder().nombre("Filete de res").descripcion("Término a elección").precio(65.0).categoria("Platos fuertes").disponible(true).build());
//             platoDao.save(Plato.builder().nombre("Pasta Alfredo").descripcion("Crema, champiñones").precio(38.0).categoria("Platos fuertes").disponible(true).build());
//             platoDao.save(Plato.builder().nombre("Refresco").descripcion("Naranja, limonada o cola").precio(8.0).categoria("Bebidas").disponible(true).build());
//             platoDao.save(Plato.builder().nombre("Agua mineral").descripcion("500ml").precio(5.0).categoria("Bebidas").disponible(true).build());
//             platoDao.save(Plato.builder().nombre("Brownie con helado").descripcion("Chocolate caliente").precio(20.0).categoria("Postres").disponible(true).build());
//         }
//     }
// }
