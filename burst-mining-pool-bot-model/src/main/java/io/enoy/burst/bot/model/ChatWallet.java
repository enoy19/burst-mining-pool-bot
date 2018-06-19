package io.enoy.burst.bot.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class ChatWallet {

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(targetEntity = Chat.class)
	private Chat chat;

	@ManyToOne(targetEntity = Wallet.class)
	private Wallet wallet;

	@Column
	private boolean notificationActive;

	@Column
	private double notificationThreshold;

	@Column
	private Date lastThresholdReached;

}
