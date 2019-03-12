package ru.rienel.clicker.activity.main;

import ru.rienel.clicker.activity.BasePresenter;
import ru.rienel.clicker.activity.BaseView;

public interface MainContract {
	interface View extends BaseView<Presenter> {
		void replaceDonut(int resourceId);
	}

	interface Presenter extends BasePresenter {

	}
}
