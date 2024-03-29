package com.mweka.natwende.route.action;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.primefaces.component.accordionpanel.AccordionPanel;
import org.primefaces.event.TabChangeEvent;

import com.mweka.natwende.helper.MessageHelper;
import com.mweka.natwende.route.vo.StopVO;
import com.mweka.natwende.types.Province;
import com.mweka.natwende.types.Status;
import com.mweka.natwende.types.Town;

@Named("StopAction")
@SessionScoped
public class StopAction extends MessageHelper<StopVO> {

	private static final long serialVersionUID = 1L;
	
	//private StopSearchVO searchVO;
	private int activeIndex;

	@PostConstruct
	//@Override
	public void init() {
		setSelectedEntity(new StopVO());
	}
	
	@Override
	public List<StopVO> getEntityList() {
		loadEntityList();
		return entityList;
	}

	@Override
	public String createEntity() {
		init();
		return viewEntity();
	}
	
	@Override
	public String saveEntity() {
		try {
			serviceLocator.getStopFacade().saveStop(getSelectedEntity());
			updateComponent(SUCCESS_DIALOG_BOX);
			executeScript(SHOW_SUCCESS_DIALOG);
		}
		catch (Exception ex) {
			onMessage(SEVERITY_ERROR, ex.getMessage());
		}
		return SUCCESS_PAGE;
	}
	
	@Override
	public String viewEntity() {
		return VIEW_PAGE;
	}
	
	@Override
	public void deleteEntity() {
		try {
			serviceLocator.getStopFacade().deleteStop(getSelectedEntity().getId());
			onMessage(SEVERITY_INFO, "Record deleted successfully");
		}
		catch (Exception ex) {
			onMessage(SEVERITY_ERROR, ex.getMessage());
		}
	}
	
	public List<Town> getTownList() {
		return Arrays.asList(Town.values());
	}
	
	public int getActiveIndex() {
		return activeIndex;
	}

	public void setActiveIndex(int activeIndex) {
		this.activeIndex = activeIndex;
	}

	public List<Province> getProvinceList() {
		if (getSelectedEntity().getTown() == null) {
			getSelectedEntity().setProvince(null);
			return Arrays.asList(Province.values());
		}
		getSelectedEntity().setProvince(getSelectedEntity().getTown().getProvince());
		return Collections.singletonList(getSelectedEntity().getTown().getProvince());
	}
	
	private void loadEntityList() {
		entityList = serviceLocator.getStopDataFacade().getAllByStatus(Status.ACTIVE);
	}
	
	public String onDialogClose() {
		executeScript(HIDE_SUCCESS_DIALOG);
		return LIST_PAGE;
	}
	
	public void onTabChange(TabChangeEvent event) {
		AccordionPanel accordion = (AccordionPanel) event.getTab().getParent();
		activeIndex = Integer.valueOf(accordion.getActiveIndex());
	}
	
	private static final String LIST_PAGE = "/admin/adminPanel?faces-redirect=true&i=2";
	private static final String VIEW_PAGE = "/admin/route/stopView?faces-redirect=true&i=2";
	private static final String SUCCESS_PAGE = "/admin/route/stopSuccess?faces-redirect=true";
	private static transient final String SHOW_SUCCESS_DIALOG = "PF('var_SuccessDlg').show();";
	private static transient final String HIDE_SUCCESS_DIALOG = "PF('var_SuccessDlg').hide();";
	private static transient final String SUCCESS_DIALOG_BOX = "@widgetVar(var_SuccessDlg)";

}
