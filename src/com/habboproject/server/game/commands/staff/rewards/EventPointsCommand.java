package com.habboproject.server.game.commands.staff.rewards;

import com.habboproject.server.boot.Comet;
import com.habboproject.server.config.CometSettings;
import com.habboproject.server.config.Locale;
import com.habboproject.server.game.commands.ChatCommand;
import com.habboproject.server.game.players.data.PlayerData;
import com.habboproject.server.network.NetworkManager;
import com.habboproject.server.network.messages.outgoing.notification.NotificationMessageComposer;
import com.habboproject.server.network.sessions.Session;
import com.habboproject.server.storage.queries.player.PlayerDao;

/**
 * Created by brend on 06/03/2017.
 */
public class EventPointsCommand extends ChatCommand {
    @Override
    public void execute(Session client, String[] params) {
        String image = Comet.getServer().getConfig().get("comet.notification.avatar.prefix");

        if (params.length != 1) {
            EventPointsCommand.sendNotif("Oops! Voc\u00ea deve digitar apenas o nome do usu\u00e1rio que ir\u00e1 receber o ponto.", client);
            return;
        }

        String username = params[0];
        if (username.isEmpty()) {
            EventPointsCommand.sendNotif("Oops! O nome de usu\u00e1rio n\u00e3o pode estar em branco.", client);
            return;
        }

        if (username.contains(";")) {
            for (String nome : username.split("[;]")) {
                Session session = NetworkManager.getInstance().getSessions().getByPlayerUsername(nome);
                if (session != null) {
                    PlayerDao.updateEventPoints(nome);

                    String notif = "";
                    if (CometSettings.enableEventWinnerReward) {
                        if (CometSettings.eventWinnerRewardType.equals("diamonds")) {
                            session.getPlayer().getData().increasePoints(CometSettings.eventWinnerRewardQuantity);
                            session.getPlayer().getData().save();
                            session.getPlayer().sendBalance();
                            notif = String.valueOf(notif) + CometSettings.eventWinnerRewardQuantity + (CometSettings.eventWinnerRewardQuantity > 1 ? "diamantes" : "diamante");
                            notif = String.valueOf(notif) + " e " + CometSettings.defaultEventPointsQuantity + (CometSettings.defaultEventPointsQuantity > 1 ? "pontos" : "ponto") + " de evento.";
                        } else if (CometSettings.eventWinnerRewardType.equals("credits")) {
                            session.getPlayer().getData().increaseCredits(CometSettings.eventWinnerRewardQuantity);
                            session.getPlayer().getData().save();
                            session.getPlayer().sendBalance();
                            notif = String.valueOf(notif) + CometSettings.eventWinnerRewardQuantity + (CometSettings.eventWinnerRewardQuantity > 1 ? "moedas" : "moeda");
                            notif = String.valueOf(notif) + " e " + CometSettings.defaultEventPointsQuantity + (CometSettings.defaultEventPointsQuantity > 1 ? "pontos" : "ponto") + " de evento.";
                        } else if (CometSettings.eventWinnerRewardType.equals("duckets")) {
                            session.getPlayer().getData().increaseActivityPoints(CometSettings.eventWinnerRewardQuantity);
                            session.getPlayer().getData().save();
                            session.getPlayer().sendBalance();
                            notif = String.valueOf(notif) + CometSettings.eventWinnerRewardQuantity + (CometSettings.eventWinnerRewardQuantity > 1 ? "ducket" : "duckets");
                            notif = String.valueOf(notif) + " e " + CometSettings.defaultEventPointsQuantity + (CometSettings.defaultEventPointsQuantity > 1 ? "pontos" : "ponto") + " de evento.";
                        }
                    }

                    EventPointsCommand.sendNotif("Voc\u00ea recebeu " + notif, (Session)session);
                } else {
                    PlayerData playerData = PlayerDao.getDataByUsername(nome);
                    if (playerData != null) {
                        PlayerDao.updateEventPoints(nome);
                        if (CometSettings.eventWinnerRewardType.equals("diamonds")) {
                            playerData.increasePoints(CometSettings.eventWinnerRewardQuantity);
                        } else if (CometSettings.eventWinnerRewardType.equals("credits")) {
                            playerData.increaseCredits(CometSettings.eventWinnerRewardQuantity);
                        } else if (CometSettings.eventWinnerRewardType.equals("duckets")) {
                            playerData.increaseActivityPoints(CometSettings.eventWinnerRewardQuantity);
                        }

                        playerData.save();
                    }
                }
            }

            String motd = "Os usu\u00e1rios ";

            for (String nome : username.split("[;]")) {
                motd = motd + nome + ", ";
            }

            EventPointsCommand.sendNotif(String.valueOf(motd) + "foram pagos.", client);

            if (CometSettings.enableEventWinnerNotification) {
                NetworkManager.getInstance().getSessions().broadcast(new NotificationMessageComposer("frank", String.valueOf(motd) + "ganharam o evento. Parabéns!"));
            }
        } else {
            Session session = NetworkManager.getInstance().getSessions().getByPlayerUsername(username);
            if (session != null) {
                PlayerDao.updateEventPoints(username);

                String notif = "";
                if (CometSettings.enableEventWinnerReward) {
                    if (CometSettings.eventWinnerRewardType.equals("diamonds")) {
                        session.getPlayer().getData().increasePoints(CometSettings.eventWinnerRewardQuantity);
                        session.getPlayer().getData().save();
                        session.getPlayer().sendBalance();
                        notif = String.valueOf(notif) + CometSettings.eventWinnerRewardQuantity + (CometSettings.eventWinnerRewardQuantity > 1 ? " diamantes" : " diamante");
                        notif = String.valueOf(notif) + " e " + CometSettings.defaultEventPointsQuantity + (CometSettings.defaultEventPointsQuantity > 1 ? " pontos" : " ponto") + " de evento.";
                    } else if (CometSettings.eventWinnerRewardType.equals("credits")) {
                        session.getPlayer().getData().increaseCredits(CometSettings.eventWinnerRewardQuantity);
                        session.getPlayer().getData().save();
                        session.getPlayer().sendBalance();
                        notif = String.valueOf(notif) + CometSettings.eventWinnerRewardQuantity + (CometSettings.eventWinnerRewardQuantity > 1 ? " moedas" : " moeda");
                        notif = String.valueOf(notif) + " e " + CometSettings.defaultEventPointsQuantity + (CometSettings.defaultEventPointsQuantity > 1 ? " pontos" : " ponto") + " de evento.";
                    } else if (CometSettings.eventWinnerRewardType.equals("duckets")) {
                        session.getPlayer().getData().increaseActivityPoints(CometSettings.eventWinnerRewardQuantity);
                        session.getPlayer().getData().save();
                        session.getPlayer().sendBalance();
                        notif = String.valueOf(notif) + CometSettings.eventWinnerRewardQuantity + (CometSettings.eventWinnerRewardQuantity > 1 ? " ducket" : " duckets");
                        notif = String.valueOf(notif) + " e " + CometSettings.defaultEventPointsQuantity + (CometSettings.defaultEventPointsQuantity > 1 ? " pontos" : " ponto") + " de evento.";
                    }
                }

                EventPointsCommand.sendNotif("Voc\u00ea recebeu " + notif, session);

                String motd = "O usu\u00e1rio " + username;

                EventPointsCommand.sendNotif(String.valueOf(motd) + " recebeu" + notif, client);

                if (CometSettings.enableEventWinnerNotification) {
                    NetworkManager.getInstance().getSessions().broadcast(new NotificationMessageComposer(image.replace("{0}", session.getPlayer().getData().getUsername()), String.valueOf(motd) + " ganhou o evento. " + (username.equals("trylix") ? "Fa\u00e7a sexo com ele! e.e" : "Parab\u00e9ns!")));
                }

                return;
            }
            PlayerData playerData = PlayerDao.getDataByUsername(username);
            if (playerData != null) {
                PlayerDao.updateEventPoints(username);

                if (CometSettings.eventWinnerRewardType.equals("diamonds")) {
                    playerData.increasePoints(CometSettings.eventWinnerRewardQuantity);
                } else if (CometSettings.eventWinnerRewardType.equals("credits")) {
                    playerData.increaseCredits(CometSettings.eventWinnerRewardQuantity);
                } else if (CometSettings.eventWinnerRewardType.equals("duckets")) {
                    playerData.increaseActivityPoints(CometSettings.eventWinnerRewardQuantity);
                }
                playerData.save();
                return;
            }
        }

        EventPointsCommand.sendNotif("Oops! Ocorreu um erro e n\u00e3o foi poss\u00edvel entregar o ponto ao usu\u00e1rio.", client);
    }

    @Override
    public String getPermission() {
        return "eventpoint_command";
    }

    @Override
    public String getDescription() {
        return Locale.get("command.eventpoint.description");
    }
}
