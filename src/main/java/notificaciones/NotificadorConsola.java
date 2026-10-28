package notificaciones;

import interfaces.Notificador;

public class NotificadorConsola implements Notificador {
    @Override
    public void enviar(String mensaje) {
        // Notificación simulada para la demostración académica.
        System.out.println("[CONTROL DE ACCESO] " + mensaje);
    }
}
