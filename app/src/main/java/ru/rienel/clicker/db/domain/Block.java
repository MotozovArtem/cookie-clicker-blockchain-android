package ru.rienel.clicker.db.domain;

import java.util.Date;

public class Block {
	private Integer id;
	private String message;
	private Integer goal;
	private Date creationTime;
	private String hashOfPreviousBlock;
	private String hashOfBlock;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getGoal() {
		return goal;
	}

	public void setGoal(Integer goal) {
		if (goal < 0) {
			throw new IllegalArgumentException("Goal cannot be less than 0");
		}
		this.goal = goal;
	}

	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	public String getHashOfPreviousBlock() {
		return hashOfPreviousBlock;
	}

	public void setHashOfPreviousBlock(String hashOfPreviousBlock) {
		this.hashOfPreviousBlock = hashOfPreviousBlock;
	}

	public String getHashOfBlock() {
		return hashOfBlock;
	}

	public void setHashOfBlock(String hashOfBlock) {
		this.hashOfBlock = hashOfBlock;
	}
}
