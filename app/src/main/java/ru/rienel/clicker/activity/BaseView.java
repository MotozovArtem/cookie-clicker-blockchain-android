package ru.rienel.clicker.activity;

public interface BaseView<T extends BasePresenter> {
	void setPresenter(T presenter);
}
