/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.powersystem.seguridad.servicios;

import com.powersystem.exception.ExcepcionManager;
import com.powersystem.seguridad.entidades.Usuario;
import com.powersystem.util.ServicioBase;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ServicioLogin extends ServicioBase {

    private static final long serialVersionUID = 1L;

    public void iniciarSesion(Usuario usuario) {

        try {
            Usuario usuarioNuevo = new Usuario();

            usuarioNuevo.setActivo(0);
            usuarioNuevo.setIdUsuario(new Long("2"));
            usuarioNuevo.setLogin("nombretest");
            usuarioNuevo.setPassword("1234567876543");

            guardar(usuarioNuevo);

            /**
             * em.createQuery(consulta, BitacoraGrupo.class)
             * .setParameter("sistema", sistema) .setParameter("grupo", grupo)
             * .setFirstResult(inicio).setMaxResults(cantidad)
             * .getResultList();*
             */
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }

    }

}
