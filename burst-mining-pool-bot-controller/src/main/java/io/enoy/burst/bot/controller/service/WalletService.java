package io.enoy.burst.bot.controller.service;

import io.enoy.burst.bot.model.*;
import io.enoy.burst.bot.model.repositories.ChatRepository;
import io.enoy.burst.bot.model.repositories.ChatWalletRepository;
import io.enoy.burst.bot.model.repositories.WalletDataRepository;
import io.enoy.burst.bot.model.repositories.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class WalletService {

    private final ChatRepository chatRepository;
    private final WalletRepository walletRepository;
    private final WalletDataRepository walletDataRepository;
    private final ChatWalletRepository chatWalletRepository;

    public List<Wallet> getWallets() {
        return walletRepository.findAll();
    }

    public Set<Wallet> getWalletsOfChat(String chatId) {
        final Chat chat = getChat(chatId);
        return chatWalletRepository.findAllByChat(chat)
                .stream()
                .map(ChatWallet::getWallet)
                .collect(Collectors.toSet());
    }

    public boolean hasChatLinkedWallets(String chatId) {
        final Chat chat = getChat(chatId);
        return !chatWalletRepository.findAllByChat(chat).isEmpty();
    }

    public Set<WalletData> getLatestWalletData(Collection<Wallet> wallets) {
        return
                wallets.stream()
                        .map(this::getLatestWalletData)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .sorted(Comparator.comparing(WalletData::getId))
                        .collect(Collectors.toSet());
    }

    public Optional<WalletData> getLatestWalletData(Wallet wallet) {
        final WalletData walletData = walletDataRepository.findFirstByWallet_IdOrderByTimestampDesc(wallet.getId());
        return Optional.ofNullable(walletData);
    }

    /**
     * @return optional list of wallet data if there were exactly two entries otherwise empty optional
     */
    public Optional<List<WalletData>> getLatestTwoWalletData(Wallet wallet) {
        final List<WalletData> walletData = walletDataRepository.findFirst2ByWallet_IdOrderByTimestampDesc(wallet.getId());
        if (walletData.size() == 2) {
            return Optional.of(walletData);
        }
        return Optional.empty();
    }

    public List<Wallet> getOnlyRegisteredWallets(List<String> walletIds) {
        return walletRepository.findAllByIdIn(walletIds);
    }

    /**
     * Registers a wallet if it does not exist
     *
     * @see WalletRepository
     */
    public synchronized void registerWallet(String burstAddressString) {
        burstAddressString = burstAddressString.trim();

        Optional<Wallet> walletOpt = walletRepository.findById(burstAddressString);

        if (!walletOpt.isPresent()) {
            log.debug("registering wallet: {}", burstAddressString);
            Wallet wallet = new Wallet();
            wallet.setId(burstAddressString);

            walletRepository.save(wallet);
        } else {
            log.debug("wallet \"{}\" already exists. Not registering", burstAddressString);
        }
    }

    public boolean toggleChatNotificationsActive(String chatId, String burstAddress) {
        final boolean newValue = !isChatNotificationsActive(chatId, burstAddress);
        setChatNotificationsActive(chatId, burstAddress, newValue);
        return newValue;
    }

    public Optional<ChatWallet> getChatWallet(Chat chat, Wallet wallet) {
        return chatWalletRepository.findByChatAndWallet(chat, wallet);
    }

    public Optional<ChatWallet> getChatWallet(String chatId, String burstAddress) {
        final Chat chat = getChat(chatId);
        final Optional<Wallet> walletOpt = getWallet(burstAddress);

        if (!walletOpt.isPresent()) {
            return Optional.empty();
        }

        final Wallet wallet = walletOpt.get();

        return getChatWallet(chat, wallet);
    }

    /**
     * @throws NoSuchElementException when {@link ChatWallet} was not found
     */
    public boolean isChatNotificationsActive(String chatId, String burstAddress) throws NoSuchElementException {
        return getChatWallet(chatId, burstAddress).get().isNotificationActive();
    }

    public void setChatNotificationsActive(String chatId, String burstAddress, boolean state) throws NoSuchElementException {
        Optional<ChatWallet> chatWalletOpt = getChatWallet(chatId, burstAddress);
        ChatWallet chatWallet = chatWalletOpt.get();

        chatWallet.setNotificationActive(state);

        chatWalletRepository.save(chatWallet);
    }

    /**
     * @throws NoSuchElementException when wallet does not exist
     */
    private Optional<Wallet> getWallet(String burstAddress) throws NoSuchElementException {
        return walletRepository.findById(burstAddress);
    }

    /**
     * sets the notification threshold of a chat
     *
     * @throws IllegalArgumentException if value is negative
     */
    public void setChatNotificationThreshold(String chatId, String burstAddress, double value) throws NoSuchElementException {
        if (value < 0) {
            throw new IllegalArgumentException("value must not be negative");
        }

        Optional<ChatWallet> chatWalletOpt = getChatWallet(chatId, burstAddress);
        ChatWallet chatWallet = chatWalletOpt.get();

        chatWallet.setNotificationThreshold(value);
        chatWalletRepository.save(chatWallet);
    }

    /**
     * links a chat to a wallet and vise versa
     *
     * @throws java.util.NoSuchElementException when wallet was not found
     * @see #registerWallet(String)
     * @see #unlinkWallet(String, String)
     */
    public synchronized void linkWallet(String burstAddress, String chatId) throws NoSuchElementException {
        final Optional<Wallet> walletOpt = getWallet(burstAddress);
        final Wallet wallet = walletOpt.get();

        final Chat chat = getChat(chatId);

        if (!getChatWallet(chat, wallet).isPresent()) {
            ChatWallet chatWallet = new ChatWallet();
            chatWallet.setChat(chat);
            chatWallet.setWallet(wallet);
            chatWalletRepository.save(chatWallet);
        }
    }

    /**
     * @return the chat with the matching id. Creates it if it does not exist
     */
    private Chat getChat(String chatId) {
        final Optional<Chat> chatOpt = chatRepository.findById(chatId);
        final Chat chat;

        chat = chatOpt.orElseGet(() -> createChat(chatId));

        return chat;
    }

    private Chat createChat(String chatId) {
        Chat chat;
        log.debug("creating non-existent chat: {}", chatId);
        final Chat newChat = new Chat();
        newChat.setId(chatId);

        chat = chatRepository.save(newChat);
        return chat;
    }

    /**
     * unlinks a chat from a wallet and vise versa. If wallet is not linked. it will do nothing.
     *
     * @see #registerWallet(String)
     * @see #linkWallet(String, String)
     */
    @Transactional
    public synchronized void unlinkWallet(String chatId, String burstAddress) throws NoSuchElementException {
        final Optional<Wallet> walletOpt = getWallet(burstAddress);
        if (!walletOpt.isPresent()) {
            return;
        }

        final Wallet wallet = walletOpt.get();
        final Chat chat = getChat(chatId);

        Optional<ChatWallet> chatWallet = getChatWallet(chat, wallet);
        chatWallet.ifPresent(chatWalletRepository::delete);
    }

    /**
     * @return List of {@link ChatWallet} that have notifications activated
     */
    public List<ChatWallet> getNotificationChatWallets() {
        return chatWalletRepository.findAllByNotificationActive(true);
    }

    public double getPendingGrowth(Wallet wallet) {
        final List<WalletData> walletDataList = walletDataRepository.findAllByWallet(wallet);
        return accumulateWalletPendingGrowth(walletDataList);
    }

    public double getPendingGrowth(Wallet wallet, Date since) {
        final List<WalletData> walletDataList = getWalletDataSince(wallet, since);
        return accumulateWalletPendingGrowth(walletDataList);
    }

    public double getPayouts(Wallet wallet) {
        final List<WalletData> walletDataList = walletDataRepository.findAllByWallet(wallet);
        return accumulateWalletPayouts(walletDataList);
    }

    public double getPayouts(Wallet wallet, Date since) {
        final List<WalletData> walletDataList = getWalletDataSince(wallet, since);
        return accumulateWalletPayouts(walletDataList);
    }

    public List<WalletData> getWalletDataSince(Wallet wallet, Date since) {
        return walletDataRepository.findAllByWalletAndTimestampAfter(wallet, since);
    }

    public List<WalletData> getAllWalletData(Wallet wallet) {
        return walletDataRepository.findAllByWallet(wallet);
    }

    private double accumulateWalletPendingGrowth(List<WalletData> walletDataList) {
        return accumulateWalletPendingChange(walletDataList, change -> change > 0);
    }

    private double accumulateWalletPayouts(List<WalletData> walletDataList) {
        final double payouts = accumulateWalletPendingChange(walletDataList, change -> change < 0);
        return Math.abs(payouts);
    }

    private double accumulateWalletPendingChange(List<WalletData> walletDataList, Function<Double, Boolean> countCondition) {
        double pendingAccumulated = 0;

        if (walletDataList.size() >= 2) {
            double prevPending = walletDataList.get(0).getPending();
            for (int i = 1; i < walletDataList.size(); i++) {
                final WalletData walletData = walletDataList.get(i);
                final double pending = walletData.getPending();

                double change = pending - prevPending;
                prevPending = pending;
                if (countCondition.apply(change)) {
                    pendingAccumulated += change;
                }
            }
        }
        return pendingAccumulated;
    }

    public void updateLastThresholdReached(Long chatWalletId, Date date) {
        chatWalletRepository.findById(chatWalletId).ifPresent(chatWallet -> {
            chatWallet.setLastThresholdReached(date);
            chatWalletRepository.save(chatWallet);
        });
    }

    public List<Chat> getChats() {
        return chatRepository.findAll();
    }

    public void setThresholdMode(String chatId, String burstAddress, ThresholdMode mode) throws NoSuchElementException {
        Optional<ChatWallet> chatWalletOpt = getChatWallet(chatId, burstAddress);
        ChatWallet chatWallet = chatWalletOpt.get();

        chatWallet.setThresholdMode(mode);
        chatWalletRepository.save(chatWallet);
    }

    @Transactional
    public void deleteWalletDataBefore(LocalDateTime deleteBefore) {
        final Date deleteBeforeDate = Timestamp.valueOf(deleteBefore);
        walletDataRepository.deleteByTimestampBefore(deleteBeforeDate);
    }

    public long countAllWalletData() {
        return walletDataRepository.count();
    }
}
