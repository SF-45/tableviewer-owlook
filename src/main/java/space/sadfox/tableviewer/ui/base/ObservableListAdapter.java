package space.sadfox.tableviewer.ui.base;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ObservableListAdapter extends XmlAdapter<ArrayList<String>, List<StringProperty>> {

	@Override
	public List<StringProperty> unmarshal(ArrayList<String> v) throws Exception {
		return FXCollections.observableList(v.stream().map(s -> new SimpleStringProperty(s)).collect(Collectors.toList()));
	}

	@Override
	public ArrayList<String> marshal(List<StringProperty> v) throws Exception {
		ArrayList<String> vv = new ArrayList<>();
		vv.addAll(v.stream().map(sp -> sp.get()).collect(Collectors.toList()));
		return vv;
	}

//	@Override
//	public ObservableList<StringProperty> unmarshal(List<String> v) throws Exception {
//		return FXCollections.observableList(
//				v.stream().map(s -> new SimpleStringProperty(s)).collect(Collectors.toList())
//				);
//	}
//
//	@Override
//	public List<String> marshal(List<StringProperty> v) throws Exception {
//		return v.stream().map(sp -> sp.get()).collect(Collectors.toList());
//	}

}
