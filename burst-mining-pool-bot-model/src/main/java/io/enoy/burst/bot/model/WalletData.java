package io.enoy.burst.bot.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@EqualsAndHashCode(of = {"id"})
public final class WalletData {

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(targetEntity = Wallet.class)
	private Wallet wallet;

	@Column
	private double pending;

	@Column
	private double share;

	/**
	 * the effective capacity in terabytes
	 */
	@Column
	private double effectiveCapacity;

	@Column
	private long confirmedDeadlines;

	@Column
	private Date timestamp;

}
