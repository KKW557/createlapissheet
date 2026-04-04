package icu.suc.createlapissheet.client.compat.rei.category

import com.zurrtum.create.AllItems
import com.zurrtum.create.client.compat.rei.CreateCategory
import com.zurrtum.create.client.compat.rei.renderer.TwoIconRenderer
import com.zurrtum.create.client.foundation.gui.AllGuiTextures
import com.zurrtum.create.client.foundation.gui.render.FanRenderState
import com.zurrtum.create.content.processing.recipe.ProcessingOutput
import icu.suc.createlapissheet.Blocks
import icu.suc.createlapissheet.Items
import icu.suc.createlapissheet.MOD_ID
import icu.suc.createlapissheet.compat.ReiCommon
import icu.suc.createlapissheet.compat.rei.display.FanWololoDisplay
import icu.suc.createlapissheet.content.processing.mansion.EvokerMansionBlock
import me.shedaniel.math.Point
import me.shedaniel.math.Rectangle
import me.shedaniel.rei.api.client.gui.Renderer
import me.shedaniel.rei.api.client.gui.widgets.Widget
import me.shedaniel.rei.api.client.gui.widgets.Widgets
import me.shedaniel.rei.api.common.entry.EntryIngredient
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import org.joml.Matrix3x2f

class FanWololoCategory : CreateCategory<FanWololoDisplay>() {
    override fun addWidgets(
        widgets: MutableList<Widget>,
        display: FanWololoDisplay,
        bounds: Rectangle
    ) {
        val outputs = mutableListOf<Point>()
        val outputIngredients = mutableListOf<EntryIngredient>()
        val chances = mutableListOf<Point>()
        val chanceIngredients = mutableListOf<EntryIngredient>()

        val input = Point(bounds.x + 26, bounds.y + 53)

        addOutputData(
            ProcessingOutput(display.output),
            bounds.x + 146,
            bounds.y + 53,
            outputs,
            outputIngredients,
            chances,
            chanceIngredients
        )

        widgets += Widgets.createDrawableWidget { graphics: GuiGraphics, _, _, _ ->
            drawSlotBackground(graphics, outputs, input)
            drawChanceSlotBackground(graphics, chances)

            AllGuiTextures.JEI_SHADOW.render(graphics, bounds.x + 51, bounds.y + 32)
            AllGuiTextures.JEI_LIGHT.render(graphics, bounds.x + 70, bounds.y + 44)
            AllGuiTextures.JEI_LONG_ARROW.render(graphics, bounds.x + 59, bounds.y + 56)

            graphics.guiRenderState.submitPicturesInPictureState(
                FanRenderState(
                    Matrix3x2f(graphics.pose()),
                    bounds.x + 61,
                    bounds.y + 9,
                    Blocks.EVOKER_MANSION.defaultBlockState()
                        .setValue(EvokerMansionBlock.EVOKER, EvokerMansionBlock.Evoker.WOLOLO)
                        .setValue(EvokerMansionBlock.NAUSEA, display.nausea)
                )
            )
        }

        widgets += createInputSlot(input).entries(display.input)

        outputs.zip(outputIngredients).forEach { (point, ingredient) ->
            widgets += createOutputSlot(point).entries(ingredient)
        }

        chances.zip(chanceIngredients).forEach { (point, ingredient) ->
            widgets += createOutputSlot(point).entries(ingredient)
        }
    }

    override fun getCategoryIdentifier() = ReiCommon.FAN_WOLOLO

    override fun getTitle() = Component.translatable("${MOD_ID}.recipe.${ReiCommon.FAN_WOLOLO.path}")

    override fun getIcon(): Renderer = TwoIconRenderer(AllItems.PROPELLER, Items.SAFE_EVOKER_MANSION)

    override fun getDisplayHeight() = 82
}