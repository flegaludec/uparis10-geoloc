package controllers;

import java.io.File;

import models.Description;
import play.*;
import play.exceptions.TemplateNotFoundException;
import play.i18n.Messages;
import play.mvc.*;

@With(Secure.class)
public class Descriptions extends CRUD {
	// ...

	// Will save your object
	public static void create(Description object, File photo) {
		/* Get the current type of controller and test it on non-empty */
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);

		/* We perform validation of the generated crud module form fields */
		validation.valid(object);
		if (validation.hasErrors()) {
			renderArgs.put("error", Messages.get("crud.hasErrors"));
			try {
				render(request.controller.replace(".", "/") + "/blank.html",
						type, object);
			} catch (TemplateNotFoundException e) {
				render("CRUD/blank.html", type, object);
			}
		}

		/* Save our object into db */
		object._save();

		if (photo != null) {
			notFoundIfNull(photo);
			File newFile = Play.getFile("/public/images/upload/"
					+ photo.getName());
			photo.renameTo(newFile);
			photo.delete();
			flash.success("Success " + newFile.getAbsolutePath());
		}
		/* Show messages */
		flash.success(Messages.get("crud.created", type.modelName));
		if (params.get("_save") != null) {
			redirect(request.controller + ".list");
		}
		if (params.get("_saveAndAddAnother") != null) {
			redirect(request.controller + ".blank");
		}
	}
}