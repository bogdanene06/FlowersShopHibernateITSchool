package entity.dao;

import entity.Client;

import java.util.List;
import java.util.Optional;

public interface ClientDAO {

    void createClient();
    Optional<Client> getClientByName(String name);
    List<Client> findAllClients();

    Optional<Client> findClientById();
    void updateClientById(Client client);

    void deleteClientById();
}
