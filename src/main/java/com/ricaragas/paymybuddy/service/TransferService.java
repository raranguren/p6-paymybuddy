package com.ricaragas.paymybuddy.service;

import com.ricaragas.paymybuddy.exceptions.InvalidAmountException;
import com.ricaragas.paymybuddy.exceptions.NotEnoughBalanceException;
import com.ricaragas.paymybuddy.exceptions.NotFoundException;
import com.ricaragas.paymybuddy.exceptions.TextTooShortException;
import com.ricaragas.paymybuddy.model.Connection;
import com.ricaragas.paymybuddy.model.Transfer;
import com.ricaragas.paymybuddy.repository.TransferRepository;
import com.ricaragas.paymybuddy.dto.TransferRowDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransferService {

    @Autowired
    TransferRepository transferRepository;

    @Autowired
    ConnectionService connectionService;

    @Autowired
    WalletService walletService;

    public void save(Connection connection, String description, int amountInCents) {
        var transfer = new Transfer();
        transfer.setConnection(connection);
        transfer.setAmountInCents(amountInCents);
        transfer.setDescription(description);
        transfer.setTimeCompleted(new Timestamp(System.currentTimeMillis()));
        transferRepository.save(transfer);
    }

    public List<TransferRowDTO> getSentTransfers() {
        return transferRepository
                .findAllByConnection_creatorOrderByTimeCompletedDesc(walletService.getActiveWallet())
                .stream()
                .map(transfer -> new TransferRowDTO(
                        transfer.getConnection().getName(),
                        transfer.getDescription(),
                        transfer.getAmountInEuros(),
                        transfer.getTimeCompleted()))
                .collect(Collectors.toList());
    }

    public void createTransfer(Long connectionId, String description, double amountInEuros)
            throws NotFoundException, TextTooShortException, NotEnoughBalanceException, InvalidAmountException {

        var transfer = new Transfer();
        if (description == null || "".equals(description)) throw new TextTooShortException();
        transfer.setDescription(description);

        int amountInCents = Math.toIntExact(Math.round(amountInEuros * 100));
        if (amountInCents <= 0) throw new InvalidAmountException(); // can't use pay function to get money
        transfer.setAmountInCents(amountInCents);

        var connection = connectionService.findById(connectionId)
                .orElseThrow(NotFoundException::new);
        transfer.setConnection(connection);

        walletService.doBalanceUpdate(connection.getCreator(), -amountInCents); // throws NotEnoughBalanceException
        walletService.doBalanceUpdate(connection.getTarget(), amountInCents);
        transferRepository.save(transfer);
    }

    public double getBalanceNeededForTransfer(String email, Double amountInEuros) {
        try {
            return amountInEuros - walletService.getBalanceInEuros(email);
        } catch (NotFoundException e) {
            return 0;
        }
    }
}
